import React, { useState, useEffect } from 'react';
import Navbar from './Navbar';
import { useAuth } from '../contexts/AuthContext';

const ESTADO_OPCIONES = ['disponible', 'no disponible', 'agotado'];

const FORM_INICIAL = {
  nombre: '',
  descripcion: '',
  duracion: '',
  precio: '',
  estado: 'disponible',
};

export default function AdminPanel() {
  const { token } = useAuth();

  // Datos
  const [cursos, setCursos] = useState([]);
  const [usuarios, setUsuarios] = useState([]);

  // Loading
  const [loadingCursos, setLoadingCursos] = useState(true);
  const [loadingUsuarios, setLoadingUsuarios] = useState(true);

  // Modales
  const [modalEditarCurso, setModalEditarCurso] = useState(null); // objeto curso | null
  const [modalCrearCurso, setModalCrearCurso] = useState(false);

  // Formulario curso
  const [formCurso, setFormCurso] = useState(FORM_INICIAL);

  // Mensajes
  const [error, setError] = useState('');
  const [mensajeExito, setMensajeExito] = useState('');

  // Carga inicial paralela
  useEffect(() => {
    async function cargarDatos() {
      const headers = { Authorization: `Bearer ${token}` };

      const [resCursos, resUsuarios] = await Promise.all([
        fetch('/api/admin/cursos', { headers }),
        fetch('/api/admin/usuarios', { headers }),
      ]);

      if (resCursos.ok) {
        const data = await resCursos.json();
        setCursos(data);
      }
      setLoadingCursos(false);

      if (resUsuarios.ok) {
        const data = await resUsuarios.json();
        setUsuarios(data);
      }
      setLoadingUsuarios(false);
    }
    cargarDatos();
  }, [token]);

  // Helpers de mensajes
  function mostrarExito(msg) {
    setMensajeExito(msg);
    setError('');
    setTimeout(() => setMensajeExito(''), 3000);
  }
  function mostrarError(msg) {
    setError(msg);
    setMensajeExito('');
  }

  // --- CURSOS ---

  function abrirCrearCurso() {
    setFormCurso(FORM_INICIAL);
    setModalCrearCurso(true);
    setModalEditarCurso(null);
  }

  function abrirEditarCurso(curso) {
    setFormCurso({
      nombre: curso.nombre || '',
      descripcion: curso.descripcion || '',
      duracion: curso.duracion || '',
      precio: curso.precio !== undefined ? String(curso.precio) : '',
      estado: curso.estado || 'disponible',
    });
    setModalEditarCurso(curso);
    setModalCrearCurso(false);
  }

  function cerrarModal() {
    setModalCrearCurso(false);
    setModalEditarCurso(null);
    setFormCurso(FORM_INICIAL);
  }

  async function handleGuardarCurso(e) {
    e.preventDefault();
    const headers = {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    };

    if (modalCrearCurso) {
      // Crear
      const res = await fetch('/api/admin/cursos', {
        method: 'POST',
        headers,
        body: JSON.stringify({
          ...formCurso,
          precio: formCurso.precio !== '' ? Number(formCurso.precio) : undefined,
        }),
      });
      if (res.ok || res.status === 201) {
        const nuevo = await res.json();
        setCursos((prev) => [...prev, nuevo]);
        cerrarModal();
        mostrarExito('Curso creado correctamente');
      } else {
        mostrarError('Error al crear el curso');
      }
    } else if (modalEditarCurso) {
      // Editar
      const res = await fetch(`/api/admin/cursos/${modalEditarCurso.id}`, {
        method: 'PUT',
        headers,
        body: JSON.stringify({
          ...formCurso,
          precio: formCurso.precio !== '' ? Number(formCurso.precio) : undefined,
        }),
      });
      if (res.ok) {
        const actualizado = await res.json();
        setCursos((prev) =>
          prev.map((c) => (c.id === modalEditarCurso.id ? actualizado : c))
        );
        cerrarModal();
        mostrarExito('Curso actualizado correctamente');
      } else {
        mostrarError('Error al actualizar el curso');
      }
    }
  }

  async function handleEliminarCurso(curso) {
    if (!window.confirm('¿Eliminar este curso?')) return;
    const res = await fetch(`/api/admin/cursos/${curso.id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) {
      setCursos((prev) => prev.filter((c) => c.id !== curso.id));
      mostrarExito('Curso eliminado');
    } else {
      mostrarError('Error al eliminar el curso');
    }
  }

  // --- USUARIOS ---

  async function handleEliminarUsuario(usuario) {
    if (!window.confirm(`¿Eliminar al usuario ${usuario.nombre}?`)) return;
    const res = await fetch(`/api/admin/usuarios/${usuario.id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (res.ok) {
      setUsuarios((prev) => prev.filter((u) => u.id !== usuario.id));
      mostrarExito('Usuario eliminado');
    } else {
      mostrarError('Error al eliminar el usuario');
    }
  }

  // --- RENDER ---

  const isModalAbierto = modalCrearCurso || modalEditarCurso !== null;

  return (
    <div className="min-h-screen bg-[#EBF5FB]">
      <Navbar />

      <div className="max-w-5xl mx-auto px-4 py-8">
        {/* Mensajes globales */}
        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded text-sm">{error}</div>
        )}
        {mensajeExito && (
          <div className="mb-4 p-3 bg-green-100 text-green-700 rounded text-sm">{mensajeExito}</div>
        )}

        {/* ===== SECCIÓN CURSOS ===== */}
        <section className="mb-10">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-bold text-gray-800">Gestión de Cursos</h2>
            <button
              onClick={abrirCrearCurso}
              className="bg-[#27AE60] hover:bg-[#1E8449] text-white text-sm font-medium px-4 py-2 rounded transition-colors"
            >
              + Nuevo Curso
            </button>
          </div>

          {loadingCursos ? (
            <p className="text-sm text-gray-500">Cargando cursos...</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm border-collapse">
                <thead>
                  <tr className="bg-[#2E86C1] text-white">
                    <th className="border border-gray-200 px-3 py-2 text-left">Nombre</th>
                    <th className="border border-gray-200 px-3 py-2 text-left">Duración</th>
                    <th className="border border-gray-200 px-3 py-2 text-left">Precio</th>
                    <th className="border border-gray-200 px-3 py-2 text-left">Estado</th>
                    <th className="border border-gray-200 px-3 py-2 text-left">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {cursos.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="text-center py-4 text-gray-500">
                        No hay cursos registrados
                      </td>
                    </tr>
                  ) : (
                    cursos.map((curso, idx) => (
                      <tr
                        key={curso.id}
                        className={idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'}
                      >
                        <td className="border border-gray-200 px-3 py-2">{curso.nombre}</td>
                        <td className="border border-gray-200 px-3 py-2">{curso.duracion}</td>
                        <td className="border border-gray-200 px-3 py-2">
                          ${Number(curso.precio).toFixed(2)}
                        </td>
                        <td className="border border-gray-200 px-3 py-2 capitalize">
                          {curso.estado}
                        </td>
                        <td className="border border-gray-200 px-3 py-2">
                          <div className="flex gap-2">
                            <button
                              onClick={() => abrirEditarCurso(curso)}
                              className="bg-[#2E86C1] hover:bg-[#2471A3] text-white text-xs px-3 py-1 rounded transition-colors"
                            >
                              Editar
                            </button>
                            <button
                              onClick={() => handleEliminarCurso(curso)}
                              className="bg-red-500 hover:bg-red-600 text-white text-xs px-3 py-1 rounded transition-colors"
                            >
                              Eliminar
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </section>

        {/* ===== SECCIÓN USUARIOS ===== */}
        <section>
          <h2 className="text-lg font-bold text-gray-800 mb-4">Gestión de Usuarios</h2>

          {loadingUsuarios ? (
            <p className="text-sm text-gray-500">Cargando usuarios...</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm border-collapse">
                <thead>
                  <tr className="bg-[#2E86C1] text-white">
                    <th className="border border-gray-200 px-3 py-2 text-left">ID</th>
                    <th className="border border-gray-200 px-3 py-2 text-left">Nombre</th>
                    <th className="border border-gray-200 px-3 py-2 text-left">Email</th>
                    <th className="border border-gray-200 px-3 py-2 text-left">Fecha registro</th>
                    <th className="border border-gray-200 px-3 py-2 text-left">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {usuarios.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="text-center py-4 text-gray-500">
                        No hay usuarios registrados
                      </td>
                    </tr>
                  ) : (
                    usuarios.map((usuario, idx) => (
                      <tr
                        key={usuario.id}
                        className={idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'}
                      >
                        <td className="border border-gray-200 px-3 py-2">{usuario.id}</td>
                        <td className="border border-gray-200 px-3 py-2">{usuario.nombre}</td>
                        <td className="border border-gray-200 px-3 py-2">{usuario.email}</td>
                        <td className="border border-gray-200 px-3 py-2">
                          {usuario.fecha_registro
                            ? new Date(usuario.fecha_registro).toLocaleDateString('es-ES')
                            : '—'}
                        </td>
                        <td className="border border-gray-200 px-3 py-2">
                          {usuario.email !== 'admin@cursos.com' && (
                            <button
                              onClick={() => handleEliminarUsuario(usuario)}
                              className="bg-red-500 hover:bg-red-600 text-white text-xs px-3 py-1 rounded transition-colors"
                            >
                              Eliminar
                            </button>
                          )}
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>

      {/* ===== MODAL CREAR / EDITAR CURSO ===== */}
      {isModalAbierto && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 px-4">
          <div className="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
            <h3 className="text-base font-bold mb-4">
              {modalCrearCurso ? 'Nuevo Curso' : 'Editar Curso'}
            </h3>

            <form onSubmit={handleGuardarCurso} className="space-y-3">
              {/* Nombre */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nombre <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={formCurso.nombre}
                  onChange={(e) => setFormCurso((f) => ({ ...f, nombre: e.target.value }))}
                  placeholder="Nombre del curso"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#2E86C1]"
                />
              </div>

              {/* Descripción */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Descripción
                </label>
                <textarea
                  value={formCurso.descripcion}
                  onChange={(e) => setFormCurso((f) => ({ ...f, descripcion: e.target.value }))}
                  placeholder="Descripción del curso"
                  rows={3}
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#2E86C1] resize-none"
                />
              </div>

              {/* Duración */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Duración
                </label>
                <input
                  type="text"
                  value={formCurso.duracion}
                  onChange={(e) => setFormCurso((f) => ({ ...f, duracion: e.target.value }))}
                  placeholder="Ej: 20 horas"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#2E86C1]"
                />
              </div>

              {/* Precio */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Precio <span className="text-red-500">*</span>
                </label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  required
                  value={formCurso.precio}
                  onChange={(e) => setFormCurso((f) => ({ ...f, precio: e.target.value }))}
                  placeholder="0.00"
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#2E86C1]"
                />
              </div>

              {/* Estado */}
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Estado
                </label>
                <select
                  value={formCurso.estado}
                  onChange={(e) => setFormCurso((f) => ({ ...f, estado: e.target.value }))}
                  className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#2E86C1] bg-white"
                >
                  {ESTADO_OPCIONES.map((op) => (
                    <option key={op} value={op}>
                      {op.charAt(0).toUpperCase() + op.slice(1)}
                    </option>
                  ))}
                </select>
              </div>

              {/* Botones */}
              <div className="flex gap-3 pt-2">
                <button
                  type="button"
                  onClick={cerrarModal}
                  className="flex-1 border border-gray-300 text-gray-700 font-medium py-2 rounded hover:bg-gray-50 transition-colors text-sm"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="flex-1 bg-[#2E86C1] hover:bg-[#2471A3] text-white font-medium py-2 rounded transition-colors text-sm"
                >
                  Guardar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
