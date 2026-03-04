using BiblioTec.DTOs.Categorias;

namespace BiblioTec.Services.Interfaces
{
    public interface ICategoriaService
    {
        Task<IEnumerable<CategoriaDto>> GetAllAsync();
        Task<CategoriaDto> CreateAsync(CategoriaCreateDto dto);
        Task<CategoriaDto> UpdateAsync(int id, CategoriaCreateDto dto);
        Task DeleteAsync(int id);
    }
}
