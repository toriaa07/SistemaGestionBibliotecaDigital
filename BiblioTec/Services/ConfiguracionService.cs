using AutoMapper;
using BiblioTec.Models;
using BiblioTec.DTOs.Configuracion;
using BiblioTec.Services.Interfaces;

namespace BiblioTec.Services.Implementations
{
    public class ConfiguracionService : IConfiguracionService
    {
        private readonly BibliotecaContext _context;
        private readonly IMapper _mapper;

        public ConfiguracionService(BibliotecaContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public async Task<ConfiguracionDto> GetAsync()
        {
            var config = await _context.Configuraciones.FindAsync(1)
                ?? throw new InvalidOperationException("No se encontró la configuración del sistema.");

            return _mapper.Map<ConfiguracionDto>(config);
        }

        public async Task<ConfiguracionDto> UpdateAsync(ConfiguracionDto dto)
        {
            var config = await _context.Configuraciones.FindAsync(1)
                ?? throw new InvalidOperationException("No se encontró la configuración del sistema.");

            config.DiasPrestamo          = dto.DiasPrestamo;
            config.MaxPrestamosActivos   = dto.MaxPrestamosActivos;
            config.NotificacionesActivas = dto.NotificacionesActivas;

            await _context.SaveChangesAsync();

            return _mapper.Map<ConfiguracionDto>(config);
        }
    }
}
