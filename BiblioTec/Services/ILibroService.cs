using BiblioTec.DTOs.Libros;

namespace BiblioTec.Services.Interfaces
{
    public interface ILibroService
    {
        Task<IEnumerable<LibroDto>> GetAllAsync();
        Task<LibroDto> GetByIdAsync(int id);
        Task<LibroDto> CreateAsync(LibroCreateDto dto);
        Task<LibroDto> UpdateAsync(int id, LibroUpdateDto dto);
        Task UpdateEstadoAsync(int id, bool activo);
        Task DeleteAsync(int id);
        Task AsignarCategoriasAsync(int idLibro, List<int> categorias);
        Task QuitarCategoriaAsync(int idLibro, int idCategoria);
    }
}
