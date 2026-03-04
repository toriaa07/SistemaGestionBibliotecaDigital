using BiblioTec.DTOs.Usuarios;

namespace BiblioTec.Services.Interfaces
{
    public interface IUsuarioService
    {
        Task<IEnumerable<UsuarioDto>> GetAllAsync();
        Task<UsuarioDto> GetByIdAsync(int id);
        Task<UsuarioDto> CreateAsync(UsuarioRegisterDto dto);
        Task<UsuarioDto> UpdateAsync(int id, UsuarioUpdateDto dto);
        Task UpdateEstadoAsync(int id, UsuarioEstadoDto dto);
        Task DeleteAsync(int id);
    }
}
