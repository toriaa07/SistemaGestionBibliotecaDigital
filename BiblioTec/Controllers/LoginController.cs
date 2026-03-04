using Microsoft.AspNetCore.Mvc;

namespace BiblioTec.Controllers
{
    public class LoginController : Controller
    {
        public IActionResult Index()
        {
            return View();
        }
    }
}
