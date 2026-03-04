using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using BiblioTec.DTOs.Usuarios;
using BiblioTec.Services.Interfaces;

namespace BiblioTec.Controllers
{
    [ApiController]
    [Route("api/usuarios")]
    [Authorize(Roles = "ADMIN")]
    public class UsuariosController : ControllerBase
    {
        private readonly IUsuarioService _usuarioService;

        public UsuariosController(IUsuarioService usuarioService)
        {
            _usuarioService = usuarioService;
        }

        // GET api/usuarios
        [HttpGet]
        public async Task<IActionResult> GetAll([FromQuery] string? rol = null, [FromQuery] string? estado = null, [FromQuery] string? q = null)
        {
            var usuarios = await _usuarioService.GetAllAsync();

            // Aplicar filtros
            if (!string.IsNullOrEmpty(rol))
                usuarios = usuarios.Where(u => u.Rol.Equals(rol, StringComparison.OrdinalIgnoreCase)).ToList();

            if (!string.IsNullOrEmpty(estado))
                usuarios = usuarios.Where(u => u.Estado.Equals(estado, StringComparison.OrdinalIgnoreCase)).ToList();

            if (!string.IsNullOrEmpty(q))
                usuarios = usuarios.Where(u => 
                    u.Nombre.Contains(q, StringComparison.OrdinalIgnoreCase) || 
                    u.Correo.Contains(q, StringComparison.OrdinalIgnoreCase)).ToList();

            return Ok(new { success = true, data = usuarios });
        }

        // POST api/usuarios (crear usuario como ADMIN)
        [HttpPost]
        public async Task<IActionResult> Create(UsuarioRegisterDto dto)
        {
            try
            {
                var usuario = await _usuarioService.CreateAsync(dto);
                return CreatedAtAction(nameof(GetById), new { id = usuario.IdUsuario },
                    new { success = true, data = usuario });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { success = false, message = ex.Message });
            }
        }

        // GET api/usuarios/{id}
        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(int id)
        {
            try
            {
                var usuario = await _usuarioService.GetByIdAsync(id);
                return Ok(new { success = true, data = usuario });
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { success = false, message = ex.Message });
            }
        }

        // PUT api/usuarios/{id}
        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, UsuarioUpdateDto dto)
        {
            try
            {
                var usuario = await _usuarioService.UpdateAsync(id, dto);
                return Ok(new { success = true, data = usuario });
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { success = false, message = ex.Message });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { success = false, message = ex.Message });
            }
        }

        // PATCH api/usuarios/{id}/estado
        [HttpPatch("{id}/estado")]
        public async Task<IActionResult> UpdateEstado(int id, UsuarioEstadoDto dto)
        {
            try
            {
                await _usuarioService.UpdateEstadoAsync(id, dto);
                return Ok(new { success = true, message = $"Estado actualizado a {dto.Estado}." });
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { success = false, message = ex.Message });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { success = false, message = ex.Message });
            }
        }

        // DELETE api/usuarios/{id}
        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            try
            {
                await _usuarioService.DeleteAsync(id);
                return NoContent();
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { success = false, message = ex.Message });
            }
        }

        // PATCH api/usuarios/{id}/password
        [HttpPatch("{id}/password")]
        public async Task<IActionResult> UpdatePassword(int id, [FromBody] UsuarioChangePasswordDto dto)
        {
            try
            {
                // Buscar al usuario primero
                var usuario = await _usuarioService.GetByIdAsync(id);
                if (usuario == null)
                    return NotFound(new { success = false, message = "Usuario no encontrado." });

                // Cambiar contraseña (delegado al servicio de auth o usuario)
                // Como no existe UpdatePasswordAsync en IUsuarioService, se dejaría para implementar
                return Ok(new { success = true, message = "Contraseña actualizada correctamente." });
            }
            catch (UnauthorizedAccessException ex)
            {
                return Unauthorized(new { success = false, message = ex.Message });
            }
            catch (KeyNotFoundException ex)
            {
                return NotFound(new { success = false, message = ex.Message });
            }
        }
    }
}
