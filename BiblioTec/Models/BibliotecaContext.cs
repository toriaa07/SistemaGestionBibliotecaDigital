using Microsoft.EntityFrameworkCore;
using System.ComponentModel.DataAnnotations;

namespace BiblioTec.Models
{
    public class BibliotecaContext : DbContext
    {
        public BibliotecaContext(DbContextOptions<BibliotecaContext> options) : base(options) { }

        public DbSet<Usuario> Usuarios { get; set; }
        public DbSet<Libro> Libros { get; set; }
        public DbSet<Categoria> Categorias { get; set; }
        public DbSet<LibroCategoria> LibroCategorias { get; set; }
        public DbSet<Configuracion> Configuraciones { get; set; }
        public DbSet<Prestamo> Prestamos { get; set; }
        public DbSet<Notificacion> Notificaciones { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {

            modelBuilder.Entity<LibroCategoria>()
                .HasKey(lc => new { lc.LibroId, lc.CategoriaId });

            modelBuilder.Entity<LibroCategoria>()
                .HasOne(lc => lc.Libro)
                .WithMany(l => l.LibroCategorias)
                .HasForeignKey(lc => lc.LibroId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<LibroCategoria>()
                .HasOne(lc => lc.Categoria)
                .WithMany(c => c.LibroCategorias)
                .HasForeignKey(lc => lc.CategoriaId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Prestamo>()
                .HasOne(p => p.Usuario)
                .WithMany(u => u.Prestamos)
                .HasForeignKey(p => p.UsuarioId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Prestamo>()
                .HasOne(p => p.Libro)
                .WithMany(l => l.Prestamos)
                .HasForeignKey(p => p.LibroId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Prestamo>()
                .HasOne(p => p.Configuracion)
                .WithMany(c => c.Prestamos)
                .HasForeignKey(p => p.ConfigId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Notificacion>()
                .HasOne(n => n.Usuario)
                .WithMany(u => u.Notificaciones)
                .HasForeignKey(n => n.UsuarioId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Configuracion>(entity =>
            {
                entity.ToTable(tb =>
                {
                    tb.HasTrigger("TR_Config_ActualizarVencimientos");
                    tb.UseSqlOutputClause(false);
                });
            });

            base.OnModelCreating(modelBuilder);
        }
    }

    public class Usuario
    {
        public int UsuarioId { get; set; }
        public string Nombre { get; set; }
        public string Correo { get; set; }
        public string PasswordHash { get; set; }
        public string Rol { get; set; }
        public string Estado { get; set; }
        public DateTime FechaRegistro { get; set; }

        public ICollection<Prestamo> Prestamos { get; set; }
        public ICollection<Notificacion> Notificaciones { get; set; }
    }

    public class Libro
    {
        public int LibroId { get; set; }
        public string Titulo { get; set; }
        public string Autor { get; set; }
        public string Editorial { get; set; }
        public int? Anio { get; set; }
        public string RutaPdf { get; set; }
        public int TotalEjemplares { get; set; }
        public bool Activo { get; set; }

        public ICollection<LibroCategoria> LibroCategorias { get; set; }
        public ICollection<Prestamo> Prestamos { get; set; }
    }

    public class Categoria
    {
        public int CategoriaId { get; set; }
        public string Nombre { get; set; }

        public ICollection<LibroCategoria> LibroCategorias { get; set; }
    }

    public class LibroCategoria
    {
        public int LibroId { get; set; }
        public int CategoriaId { get; set; }

        public Libro Libro { get; set; }
        public Categoria Categoria { get; set; }
    }

    public class Configuracion
    {
        [Key]
        public int ConfigId { get; set; }
        public int DiasPrestamo { get; set; }
        public int MaxPrestamosActivos { get; set; }
        public bool NotificacionesActivas { get; set; }

        public ICollection<Prestamo> Prestamos { get; set; }
    }

    public class Prestamo
    {
        public int PrestamoId { get; set; }
        public int UsuarioId { get; set; }
        public int LibroId { get; set; }
        public int ConfigId { get; set; }
        public DateTime FechaPrestamo { get; set; }
        public DateTime FechaVencimiento { get; set; }
        public DateTime? FechaDevolucion { get; set; }
        public string Estado { get; set; }

        public Usuario Usuario { get; set; }
        public Libro Libro { get; set; }
        public Configuracion Configuracion { get; set; }
    }

    public class Notificacion
    {
        public int NotificacionId { get; set; }
        public int UsuarioId { get; set; }
        public string Tipo { get; set; }
        public string Mensaje { get; set; }
        public DateTime FechaEnvio { get; set; }
        public bool Leida { get; set; }

        public Usuario Usuario { get; set; }
    }

    public class VwDisponibilidad
    {
        public int LibroId { get; set; }
        public int Disponibles { get; set; }
    }
}