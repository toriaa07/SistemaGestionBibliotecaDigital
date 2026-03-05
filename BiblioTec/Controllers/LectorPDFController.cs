using Microsoft.AspNetCore.Mvc;
using BiblioTec.Services.Interfaces;

namespace BiblioTec.Controllers
{
    public class LectorPDFController : Controller
    {
        private readonly IPrestamoService _prestamoService;

        public LectorPDFController(IPrestamoService prestamoService)
        {
            _prestamoService = prestamoService;
        }

        // GET /LectorPDF
        public async Task<IActionResult> Index()
        {
            var userId = HttpContext.Session.GetInt32("UserId");
            if (userId == null)
                return RedirectToAction("Index", "Login");

            ViewBag.UserName = HttpContext.Session.GetString("UserName");
            ViewBag.UserRole = HttpContext.Session.GetString("UserRole");

            var prestamos = await _prestamoService.GetByUsuarioAsync(userId.Value);

            var baseUrl = $"{Request.Scheme}://{Request.Host}";

            foreach (var p in prestamos)
            {
                if (!string.IsNullOrEmpty(p.RutaPdf))
                {
                    p.RutaPdf = baseUrl + p.RutaPdf;
                }
            }

            return View(prestamos);
        }

        // POST /LectorPDF/Devolver
        [HttpPost]
        public async Task<IActionResult> Devolver(int id)
        {
            var userId = HttpContext.Session.GetInt32("UserId");
            if (userId == null)
                return RedirectToAction("Index", "Login");

            try
            {
                await _prestamoService.DevolverAsync(id);
                TempData["Success"] = "Libro devuelto correctamente.";
            }
            catch (Exception ex)
            {
                TempData["Error"] = ex.Message;
            }

            return RedirectToAction("Index");
        }
    }
}