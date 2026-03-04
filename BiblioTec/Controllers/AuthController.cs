using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using BiblioTec.DTOs.Auth;
using BiblioTec.DTOs.Usuarios;
using BiblioTec.Services.Interfaces;

namespace BiblioTec.Controllers
{
    [ApiController]
    [Route("api/auth")]
    public class AuthController : ControllerBase
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        // POST api/auth/login
        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginRequestDto dto)
        {
            try
            {
                var response = await _authService.LoginAsync(dto);
                return Ok(new { success = true, data = response });
            }
            catch (UnauthorizedAccessException ex)
            {
                return Unauthorized(new { success = false, message = ex.Message });
            }
        }

        // POST api/auth/register
        [HttpPost("register")]
        public async Task<IActionResult> Register(UsuarioRegisterDto dto)
        {
            try
            {
                var usuario = await _authService.RegisterAsync(dto);
                return CreatedAtAction(nameof(Register), new { success = true, data = usuario });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { success = false, message = ex.Message });
            }
        }

        // POST api/auth/change-password
        [HttpPost("change-password")]
        [Authorize]
        public async Task<IActionResult> ChangePassword(UsuarioChangePasswordDto dto)
        {
            try
            {
                var idUsuario = int.Parse(User.FindFirstValue(ClaimTypes.NameIdentifier)!);
                await _authService.ChangePasswordAsync(idUsuario, dto);
                return Ok(new { success = true, message = "Contraseña actualizada correctamente." });
            }
            catch (UnauthorizedAccessException ex)
            {
                return Unauthorized(new { success = false, message = ex.Message });
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { success = false, message = ex.Message });
            }
        }

        // GET api/auth/me (obtener usuario autenticado actual)
        [HttpGet("me")]
        [Authorize]
        public IActionResult GetCurrentUser()
        {
            var idUsuario = User.FindFirstValue(ClaimTypes.NameIdentifier);
            var nombreUsuario = User.FindFirstValue(ClaimTypes.Name);
            var rol = User.FindFirstValue(ClaimTypes.Role);

            if (string.IsNullOrEmpty(idUsuario))
                return Unauthorized(new { success = false, message = "Usuario no autenticado." });

            var usuario = new
            {
                id = idUsuario,
                nombre = nombreUsuario,
                rol = rol
            };

            return Ok(new { success = true, data = usuario });
        }

        // POST api/auth/logout
        [HttpPost("logout")]
        [Authorize]
        public IActionResult Logout()
        {
            // En ASP.NET Core con JWT, el logout es manejado por el cliente (eliminar el token)
            // Si usas sesiones, aquí iría Session.Clear()
            return Ok(new { success = true, message = "Sesión cerrada correctamente." });
        }
    }
}
