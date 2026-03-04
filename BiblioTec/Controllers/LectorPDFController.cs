using Microsoft.AspNetCore.Mvc;

namespace BiblioTec.Controllers
{
    public class LectorPDFController : Controller
    {
        public IActionResult Index()
        {
            return View();
        }
    }
}
