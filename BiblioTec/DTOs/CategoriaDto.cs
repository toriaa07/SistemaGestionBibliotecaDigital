namespace BiblioTec.DTOs.Categorias
{
    public class CategoriaDto
    {
        public int IdCategoria { get; set; }
        public string Nombre { get; set; }
    }

    public class CategoriaCreateDto
    {
        public string Nombre { get; set; }
    }
    public class LibroCategoriaDto
    {
        public List<int> Categorias { get; set; } = new();
    }
}
