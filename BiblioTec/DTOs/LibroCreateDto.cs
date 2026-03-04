namespace BiblioTec.DTOs.Libros
{
    public class LibroCreateDto
    {
        public string Titulo { get; set; }
        public string Autor { get; set; }
        public string Editorial { get; set; }
        public int? Anio { get; set; }
        public string RutaPdf { get; set; }
        public int TotalEjemplares { get; set; }

        public List<int> Categorias { get; set; } = new();
    }
}
