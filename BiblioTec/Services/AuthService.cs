using AutoMapper;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using BiblioTec.Models;
using BiblioTec.DTOs.Auth;
using BiblioTec.DTOs.Usuarios;
using BiblioTec.Services.Interfaces;

namespace BiblioTec.Services.Implementations
{
    public class AuthService : IAuthService
    {
        private readonly BibliotecaContext _context;
        private readonly IMapper _mapper;
        private readonly IConfiguration _config;

        public AuthService(BibliotecaContext context, IMapper mapper, IConfiguration config)
        {
            _context = context;
            _mapper = mapper;
            _config = config;
        }

        public async Task<LoginResponseDto> LoginAsync(LoginRequestDto dto)
        {
            var usuario = await _context.Usuarios
                .FirstOrDefaultAsync(u => u.Correo == dto.Correo);

            if (usuario == null)
                throw new UnauthorizedAccessException("Correo o contraseña incorrectos.");

            if (usuario.Estado == "SUSPENDIDO")
                throw new UnauthorizedAccessException("Tu cuenta está suspendida.");

            if (!BCrypt.Net.BCrypt.Verify(dto.Password, usuario.PasswordHash))
                throw new UnauthorizedAccessException("Correo o contraseña incorrectos.");

            return new LoginResponseDto
            {
                IdUsuario = usuario.UsuarioId,
                Token = GenerarToken(usuario),
                Nombre = usuario.Nombre,
                Correo = usuario.Correo,
                Rol = usuario.Rol
            };
        }

        public async Task<UsuarioDto> RegisterAsync(UsuarioRegisterDto dto)
        {
            var existe = await _context.Usuarios.AnyAsync(u => u.Correo == dto.Correo);

            if (existe) throw new InvalidOperationException("Ya existe un usuario con ese correo.");
            var usuario = _mapper.Map<Usuario>(dto);
            usuario.PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.Password);
            _context.Usuarios.Add(usuario);
            await _context.SaveChangesAsync();
            return _mapper.Map<UsuarioDto>(usuario);
        }

        public async Task ChangePasswordAsync(int idUsuario, UsuarioChangePasswordDto dto)
        {
            var usuario = await _context.Usuarios.FindAsync(idUsuario)
                ?? throw new KeyNotFoundException("Usuario no encontrado.");

            if (!BCrypt.Net.BCrypt.Verify(dto.PasswordActual, usuario.PasswordHash))
                throw new UnauthorizedAccessException("La contraseña actual es incorrecta.");

            usuario.PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.PasswordNueva);
            await _context.SaveChangesAsync();
        }

        private string GenerarToken(Usuario usuario)
        {
            var key = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(_config["Jwt:Key"]!));

            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            var claims = new[]
            {
                new Claim(ClaimTypes.NameIdentifier, usuario.UsuarioId.ToString()),
                new Claim(ClaimTypes.Email,          usuario.Correo),
                new Claim(ClaimTypes.Name,           usuario.Nombre),
                new Claim(ClaimTypes.Role,           usuario.Rol)
            };

            var token = new JwtSecurityToken(
                issuer: _config["Jwt:Issuer"],
                audience: _config["Jwt:Audience"],
                claims: claims,
                expires: DateTime.UtcNow.AddHours(8),
                signingCredentials: creds
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }
    }
}
