using AutoMapper;
using BiblioTec.Models;
using BiblioTec.DTOs.Auth;
using BiblioTec.DTOs.Usuarios;
using BiblioTec.DTOs.Libros;
using BiblioTec.DTOs.Categorias;
using BiblioTec.DTOs.Prestamos;
using BiblioTec.DTOs.Notificaciones;
using BiblioTec.DTOs.Configuracion;

namespace BiblioTec.Mappings
{
    public class MappingProfile : Profile
    {
        public MappingProfile()
        {
            CreateMap<Usuario, UsuarioDto>();

            CreateMap<UsuarioRegisterDto, Usuario>()
                .ForMember(dest => dest.PasswordHash, opt => opt.Ignore())
                .ForMember(dest => dest.Rol,          opt => opt.MapFrom(_ => "USUARIO"))
                .ForMember(dest => dest.Estado,       opt => opt.MapFrom(_ => "ACTIVO"))
                .ForMember(dest => dest.FechaRegistro, opt => opt.MapFrom(_ => DateTime.Now));

            CreateMap<UsuarioUpdateDto, Usuario>()
                .ForAllMembers(opt => opt.Condition((src, dest, srcMember) => srcMember != null));

            CreateMap<Libro, LibroDto>()
                .ForMember(dest => dest.Disponibles, opt => opt.Ignore())
                .ForMember(dest => dest.Categorias,  opt => opt.MapFrom(src =>
                    src.LibroCategorias
                       .Select(lc => lc.Categoria.Nombre)
                       .ToList()));

            CreateMap<LibroCreateDto, Libro>()
                .ForMember(dest => dest.Activo,          opt => opt.MapFrom(_ => true))
                .ForMember(dest => dest.LibroCategorias, opt => opt.Ignore());

            CreateMap<LibroUpdateDto, Libro>()
                .ForAllMembers(opt => opt.Condition((src, dest, srcMember) => srcMember != null));

            CreateMap<Categoria, CategoriaDto>();
            CreateMap<CategoriaCreateDto, Categoria>();

            CreateMap<Prestamo, PrestamoDto>()
                .ForMember(dest => dest.NombreUsuario, opt => opt.MapFrom(src => src.Usuario.Nombre))
                .ForMember(dest => dest.TituloLibro,   opt => opt.MapFrom(src => src.Libro.Titulo))
                .ForMember(dest => dest.AutorLibro,    opt => opt.MapFrom(src => src.Libro.Autor));

            CreateMap<Prestamo, PrestamoResumenDto>()
                .ForMember(dest => dest.TituloLibro, opt => opt.MapFrom(src => src.Libro.Titulo))
                .ForMember(dest => dest.AutorLibro,  opt => opt.MapFrom(src => src.Libro.Autor))
                .ForMember(dest => dest.RutaPdf,     opt => opt.MapFrom(src => src.Libro.RutaPdf))
                .ForMember(dest => dest.LibroId,     opt => opt.MapFrom(src => src.LibroId));

            CreateMap<Notificacion, NotificacionDto>();

            CreateMap<NotificacionCreateDto, Notificacion>()
                .ForMember(dest => dest.FechaEnvio, opt => opt.MapFrom(_ => DateTime.Now))
                .ForMember(dest => dest.Leida,      opt => opt.MapFrom(_ => false));

            CreateMap<Configuracion, ConfiguracionDto>();
            CreateMap<ConfiguracionDto, Configuracion>()
                .ForMember(dest => dest.ConfigId,  opt => opt.MapFrom(_ => 1))
                .ForMember(dest => dest.Prestamos, opt => opt.Ignore());
        }
    }
}
