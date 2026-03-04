namespace BiblioTec.DTOs.Notificaciones
{
    public class NotificacionDto
    {
        public int IdNotificacion { get; set; }

        public string Tipo { get; set; }
        public string Mensaje { get; set; }
        public DateTime FechaEnvio { get; set; }
        public bool Leida { get; set; }
    }

    public class NotificacionCreateDto
    {
        public int IdUsuario { get; set; }
        public string Tipo { get; set; }
        public string Mensaje { get; set; }
    }
}
