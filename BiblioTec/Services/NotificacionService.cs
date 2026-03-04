using AutoMapper;
using Microsoft.EntityFrameworkCore;
using BiblioTec.Models;
using BiblioTec.DTOs.Notificaciones;
using BiblioTec.Services.Interfaces;

namespace BiblioTec.Services.Implementations
{
    public class NotificacionService : INotificacionService
    {
        private readonly BibliotecaContext _context;
        private readonly IMapper _mapper;

        public NotificacionService(BibliotecaContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public async Task<IEnumerable<NotificacionDto>> GetByUsuarioAsync(int idUsuario)
        {
            var notificaciones = await _context.Notificaciones
                .Where(n => n.UsuarioId == idUsuario)
                .OrderByDescending(n => n.FechaEnvio)
                .ToListAsync();

            return _mapper.Map<IEnumerable<NotificacionDto>>(notificaciones);
        }

        public async Task MarcarLeidaAsync(int id, int idUsuario)
        {
            var notificacion = await _context.Notificaciones
                .FirstOrDefaultAsync(n => n.NotificacionId == id && n.UsuarioId == idUsuario)
                ?? throw new KeyNotFoundException("Notificación no encontrada.");

            notificacion.Leida = true;
            await _context.SaveChangesAsync();
        }

        public async Task MarcarTodasLeidasAsync(int idUsuario)
        {
            var pendientes = await _context.Notificaciones
                .Where(n => n.UsuarioId == idUsuario && !n.Leida)
                .ToListAsync();

            foreach (var n in pendientes)
                n.Leida = true;

            await _context.SaveChangesAsync();
        }

        public async Task<NotificacionDto> CreateAsync(NotificacionCreateDto dto)
        {
            // Verificar que el usuario destino existe
            var usuarioExiste = await _context.Usuarios.AnyAsync(u => u.UsuarioId == dto.IdUsuario);
            if (!usuarioExiste)
                throw new KeyNotFoundException("Usuario destino no encontrado.");

            var notificacion = _mapper.Map<Notificacion>(dto);
            _context.Notificaciones.Add(notificacion);
            await _context.SaveChangesAsync();

            return _mapper.Map<NotificacionDto>(notificacion);
        }

        public async Task DeleteAsync(int id)
        {
            var notificacion = await _context.Notificaciones.FindAsync(id)
                ?? throw new KeyNotFoundException("Notificación no encontrada.");

            _context.Notificaciones.Remove(notificacion);
            await _context.SaveChangesAsync();
        }
    }
}
