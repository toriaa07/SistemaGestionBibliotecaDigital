using AutoMapper;
using Microsoft.EntityFrameworkCore;
using BiblioTec.Models;
using BiblioTec.DTOs.Categorias;
using BiblioTec.Services.Interfaces;

namespace BiblioTec.Services.Implementations
{
    public class CategoriaService : ICategoriaService
    {
        private readonly BibliotecaContext _context;
        private readonly IMapper _mapper;

        public CategoriaService(BibliotecaContext context, IMapper mapper)
        {
            _context = context;
            _mapper = mapper;
        }

        public async Task<IEnumerable<CategoriaDto>> GetAllAsync()
        {
            var categorias = await _context.Categorias.ToListAsync();
            return _mapper.Map<IEnumerable<CategoriaDto>>(categorias);
        }

        public async Task<CategoriaDto> CreateAsync(CategoriaCreateDto dto)
        {
            var existe = await _context.Categorias.AnyAsync(c => c.Nombre == dto.Nombre);
            if (existe)
                throw new InvalidOperationException("Ya existe una categoría con ese nombre.");

            var categoria = _mapper.Map<Categoria>(dto);
            _context.Categorias.Add(categoria);
            await _context.SaveChangesAsync();

            return _mapper.Map<CategoriaDto>(categoria);
        }

        public async Task<CategoriaDto> UpdateAsync(int id, CategoriaCreateDto dto)
        {
            var categoria = await _context.Categorias.FindAsync(id)
                ?? throw new KeyNotFoundException("Categoría no encontrada.");

            var nombreEnUso = await _context.Categorias
                .AnyAsync(c => c.Nombre == dto.Nombre && c.CategoriaId != id);

            if (nombreEnUso)
                throw new InvalidOperationException("Ya existe una categoría con ese nombre.");

            categoria.Nombre = dto.Nombre;
            await _context.SaveChangesAsync();

            return _mapper.Map<CategoriaDto>(categoria);
        }

        public async Task DeleteAsync(int id)
        {
            var categoria = await _context.Categorias.FindAsync(id)
                ?? throw new KeyNotFoundException("Categoría no encontrada.");

            _context.Categorias.Remove(categoria);
            await _context.SaveChangesAsync();
        }
    }
}
