namespace BiblioTec.DTOs.Prestamos
{
    public class PrestamoDto
    {
        public int PrestamoId { get; set; }

        public int UsuarioId { get; set; }
        public string NombreUsuario { get; set; }

        public int LibroId { get; set; }
        public string TituloLibro { get; set; }
        public string AutorLibro { get; set; }

        public DateTime FechaPrestamo { get; set; }
        public DateTime FechaVencimiento { get; set; }
        public DateTime? FechaDevolucion { get; set; }

        public string Estado { get; set; }
    }

    
    public class PrestamoCreateDto
    {
        public int LibroId { get; set; }
    }

    public class PrestamoResumenDto
    {
        public int PrestamoId { get; set; }
        public int LibroId { get; set; }
        public string TituloLibro { get; set; }
        public string AutorLibro { get; set; }
        public string RutaPdf { get; set; }
        public DateTime FechaPrestamo { get; set; }
        public DateTime FechaVencimiento { get; set; }
        public string Estado { get; set; }
    }
}
