import React, { useEffect, useState } from 'react';
import Navbar from './Navbar';
import { useAuth } from '../contexts/AuthContext';

// ── Color map ──────────────────────────────────────────────────────────────
const COLORES_TECNOLOGIA = {
  html:       { bg: '#E67E22', simbolo: '</>' },
  css:        { bg: '#2E86C1', simbolo: '#'   },
  javascript: { bg: '#F0B429', simbolo: 'JS'  },
  js:         { bg: '#F0B429', simbolo: 'JS'  },
  git:        { bg: '#F05133', simbolo: 'GIT' },
  nodejs:     { bg: '#68A063', simbolo: 'N'   },
  node:       { bg: '#68A063', simbolo: 'N'   },
  default:    { bg: '#27AE60', simbolo: '{ }' },
};

function getTecnologia(nombre) {
  const lower = nombre.toLowerCase();
  for (const [key, val] of Object.entries(COLORES_TECNOLOGIA)) {
    if (key !== 'default' && lower.includes(key)) return val;
  }
  return COLORES_TECNOLOGIA.default;
}

// ── Component ──────────────────────────────────────────────────────────────
export default function Cursos() {
  const { token } = useAuth();

  const [cursos, setCursos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [cursosAdquiridos, setCursosAdquiridos] = useState(new Set());
  const [modalCurso, setModalCurso] = useState(null);
  const [comprando, setComprando] = useState(false);

  // ── Fetch initial data ──────────────────────────────────────────────────
  useEffect(() => {
    const headers = { Authorization: `Bearer ${token}` };

    fetch('/api/cursos', { headers })
      .then(async (resCursos) => {
        if (!resCursos.ok) throw new Error('Error al cargar los cursos');
        
        const todosCursos = await resCursos.json();
        setCursos(todosCursos);

        // Ya que el backend no tiene la tabla de relación, usamos localStorage
        // para simular la persistencia de las compras por usuario en el cliente.
        const misCursosLocal = JSON.parse(localStorage.getItem('misCursosLocal') || '[]');
        setCursosAdquiridos(new Set(misCursosLocal));
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [token]);

  // ── Buy action ──────────────────────────────────────────────────────────
  async function handleConfirmarCompra() {
    if (!modalCurso) return;
    setComprando(true);
    try {
      const res = await fetch(`/api/cursos/${modalCurso.id}/comprar`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
      });

      if (res.ok) {
        setCursosAdquiridos((prev) => {
          const nuevoSet = new Set([...prev, modalCurso.id]);
          localStorage.setItem('misCursosLocal', JSON.stringify([...nuevoSet]));
          return nuevoSet;
        });
        setModalCurso(null);
      } else {
        alert('Error al procesar la compra. Intenta de nuevo.');
      }
    } catch {
      alert('Error de red. Intenta de nuevo.');
    } finally {
      setComprando(false);
    }
  }

  // ── Render ───────────────────────────────────────────────────────────────
  return (
    <div className="min-h-screen bg-[#EBF5FB]">
      <Navbar />

      <div className="max-w-2xl mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold text-gray-800 mb-6">
          Cursos Disponibles
        </h1>

        {/* States */}
        {loading && (
          <p className="text-gray-500 text-center mt-8">Cargando cursos…</p>
        )}
        {error && (
          <p className="text-red-500 text-center mt-8">{error}</p>
        )}

        {/* Course list */}
        {!loading && !error && cursos.map((curso) => {
          const tec = getTecnologia(curso.nombre);
          const adquirido = cursosAdquiridos.has(curso.id);

          return (
            <div
              key={curso.id}
              className="bg-white rounded-lg shadow-sm border border-gray-100 flex items-center gap-4 p-4 mb-3"
            >
              {/* Icon */}
              <div
                className="w-14 h-14 rounded-lg flex items-center justify-center text-white font-bold text-lg flex-shrink-0"
                style={{ backgroundColor: tec.bg }}
              >
                {tec.simbolo}
              </div>

              {/* Info */}
              <div className="flex-1">
                <p className="font-semibold text-gray-800 text-base">
                  {curso.nombre}
                </p>
                <p className="text-sm text-gray-500">
                  Duración: {curso.duracion} horas
                </p>
                <p className="text-sm font-semibold" style={{ color: '#E67E22' }}>
                  {parseFloat(curso.precio) === 0 ? 'Gratis' : `$${parseFloat(curso.precio).toFixed(2)}`}
                </p>
              </div>

              {/* Action button */}
              {adquirido ? (
                <button
                  disabled
                  className="px-4 py-1.5 rounded text-sm font-medium bg-gray-200 text-gray-500 cursor-not-allowed"
                >
                  Adquirido
                </button>
              ) : curso.estado === 'disponible' ? (
                <button
                  onClick={() => setModalCurso(curso)}
                  className="px-4 py-1.5 rounded text-sm font-medium bg-[#27AE60] hover:bg-[#1E8449] text-white transition-colors"
                >
                  Comprar
                </button>
              ) : (
                <button
                  disabled
                  className="px-4 py-1.5 rounded text-sm font-medium bg-[#95A5A6] text-white cursor-not-allowed"
                >
                  Agotado
                </button>
              )}
            </div>
          );
        })}
      </div>

      {/* Purchase modal */}
      {modalCurso && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-sm shadow-xl">
            <h2 className="text-lg font-bold text-gray-800 mb-3">
              ¿Confirmar compra?
            </h2>
            <p className="text-gray-700 mb-1 font-medium">{modalCurso.nombre}</p>
            <p className="text-sm font-semibold mb-5" style={{ color: '#E67E22' }}>
              {parseFloat(modalCurso.precio) === 0
                ? 'Gratis'
                : `$${parseFloat(modalCurso.precio).toFixed(2)}`}
            </p>

            <div className="flex gap-3 justify-end">
              <button
                onClick={() => setModalCurso(null)}
                disabled={comprando}
                className="px-4 py-2 rounded text-sm font-medium bg-gray-200 text-gray-700 hover:bg-gray-300 transition-colors disabled:opacity-50"
              >
                Cancelar
              </button>
              <button
                onClick={handleConfirmarCompra}
                disabled={comprando}
                className="px-4 py-2 rounded text-sm font-medium text-white transition-colors disabled:opacity-50"
                style={{ backgroundColor: '#27AE60' }}
              >
                {comprando ? 'Procesando…' : 'Confirmar'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
