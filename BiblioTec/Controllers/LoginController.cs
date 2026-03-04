using Microsoft.AspNetCore.Mvc;
using BiblioTec.DTOs.Auth;
using BiblioTec.Services.Interfaces;

namespace BiblioTec.Controllers
{
    public class LoginController : Controller
    {
        private readonly IAuthService _authService;

        public LoginController(IAuthService authService)
        {
            _authService = authService;
        }

        // GET /Login
        [HttpGet]
        public IActionResult Index()
        {
            // Si ya tiene sesión, redirigir al lector
            if (HttpContext.Session.GetInt32("UserId") != null)
                return RedirectToAction("Index", "LectorPDF");

            return View();
        }

        // POST /Login
        [HttpPost]
        public async Task<IActionResult> Index(LoginRequestDto dto)
        {
            try
            {
                var response = await _authService.LoginAsync(dto);

                // Guardar datos en sesión
                HttpContext.Session.SetInt32("UserId", response.IdUsuario);
                HttpContext.Session.SetString("UserName", response.Nombre);
                HttpContext.Session.SetString("UserRole", response.Rol);

                return RedirectToAction("Index", "LectorPDF");
            }
            catch (UnauthorizedAccessException ex)
            {
                ViewBag.Error = ex.Message;
                return View();
            }
        }

        // GET /Login/Logout
        public IActionResult Logout()
        {
            HttpContext.Session.Clear();
            return RedirectToAction("Index");
        }
    }
}
