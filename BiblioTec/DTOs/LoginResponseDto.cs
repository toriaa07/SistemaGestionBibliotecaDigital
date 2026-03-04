namespace BiblioTec.DTOs.Auth
{
    public class LoginResponseDto
    {
        public int IdUsuario { get; set; }
        public string Token { get; set; }
        public string Nombre { get; set; }
        public string Correo { get; set; }
        public string Rol { get; set; }
    }
}
