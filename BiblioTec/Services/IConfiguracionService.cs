using BiblioTec.DTOs.Configuracion;

namespace BiblioTec.Services.Interfaces
{
    public interface IConfiguracionService
    {
        Task<ConfiguracionDto> GetAsync();
        Task<ConfiguracionDto> UpdateAsync(ConfiguracionDto dto);
    }
}
