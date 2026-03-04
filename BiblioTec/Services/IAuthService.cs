using BiblioTec.DTOs.Auth;
using BiblioTec.DTOs.Usuarios;

namespace BiblioTec.Services.Interfaces
{
    public interface IAuthService
    {
        Task<LoginResponseDto> LoginAsync(LoginRequestDto dto);
        Task<UsuarioDto> RegisterAsync(UsuarioRegisterDto dto);
        Task ChangePasswordAsync(int idUsuario, UsuarioChangePasswordDto dto);
    }
}
