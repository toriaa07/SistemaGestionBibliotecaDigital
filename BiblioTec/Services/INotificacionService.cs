using BiblioTec.DTOs.Notificaciones;

namespace BiblioTec.Services.Interfaces
{
    public interface INotificacionService
    {
        Task<IEnumerable<NotificacionDto>> GetByUsuarioAsync(int idUsuario);
        Task MarcarLeidaAsync(int id, int idUsuario);
        Task MarcarTodasLeidasAsync(int idUsuario);
        Task<NotificacionDto> CreateAsync(NotificacionCreateDto dto);
        Task DeleteAsync(int id);
    }
}
