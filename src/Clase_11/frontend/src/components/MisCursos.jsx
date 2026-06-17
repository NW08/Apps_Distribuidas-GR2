import React, {useEffect, useState} from 'react';
import Navbar from './Navbar';
import {useAuth} from '../contexts/AuthContext';

// ── Color map (shared logic with Cursos.jsx) ───────────────────────────────
const COLORES_TECNOLOGIA = {
    html: {bg: '#E67E22', simbolo: '</>'},
    css: {bg: '#2E86C1', simbolo: '#'},
    javascript: {bg: '#F0B429', simbolo: 'JS'},
    js: {bg: '#F0B429', simbolo: 'JS'},
    git: {bg: '#F05133', simbolo: 'GIT'},
    nodejs: {bg: '#68A063', simbolo: 'N'},
    node: {bg: '#68A063', simbolo: 'N'},
    default: {bg: '#27AE60', simbolo: '{ }'},
};

function getTecnologia(nombre) {
    const lower = nombre.toLowerCase();
    for (const [key, val] of Object.entries(COLORES_TECNOLOGIA)) {
        if (key !== 'default' && lower.includes(key)) return val;
    }
    return COLORES_TECNOLOGIA.default;
}

// ── Component ──────────────────────────────────────────────────────────────
export default function MisCursos() {
    const {token} = useAuth();

    const [cursos, setCursos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetch('/api/cursos', {
            headers: {Authorization: `Bearer ${token}`},
        })
            .then((res) => {
                if (!res.ok) throw new Error('Error al cargar tus cursos');
                return res.json();
            })
            .then((data) => {
                const misCursosLocal = JSON.parse(localStorage.getItem('misCursosLocal') || '[]');
                const setAdquiridos = new Set(misCursosLocal);
                const misCursosFiltrados = data.filter(curso => setAdquiridos.has(curso.id));
                setCursos(misCursosFiltrados);
            })
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false));
    }, [token]);

    return (
        <div className="min-h-screen bg-[#EBF5FB]">
            <Navbar/>

            <div className="max-w-2xl mx-auto px-4 py-8">
                <h1 className="text-2xl font-bold text-gray-800 mb-6">Mis Cursos</h1>

                {/* States */}
                {loading && (
                    <p className="text-gray-500 text-center mt-8">Cargando tus cursos…</p>
                )}
                {error && (
                    <p className="text-red-500 text-center mt-8">{error}</p>
                )}

                {/* Empty state */}
                {!loading && !error && cursos.length === 0 && (
                    <p className="text-gray-500 text-center mt-8">
                        No has adquirido ningún curso aún.
                    </p>
                )}

                {/* Course list */}
                {!loading && !error && cursos.map((curso) => {
                    const tec = getTecnologia(curso.nombre);

                    return (
                        <div
                            key={curso.id}
                            className="bg-white rounded-lg shadow-sm border border-gray-100 flex items-center gap-4 p-4 mb-3"
                        >
                            {/* Icon */}
                            <div
                                className="w-14 h-14 rounded-lg flex items-center justify-center text-white font-bold text-lg shrink-0"
                                style={{backgroundColor: tec.bg}}
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
                                <p className="text-sm font-semibold" style={{color: '#E67E22'}}>
                                    {parseFloat(curso.precio) === 0 ? 'Gratis' : `$${parseFloat(curso.precio).toFixed(2)}`}
                                </p>
                            </div>

                            {/* Badge */}
                            <span className="px-3 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-700">
                Adquirido
              </span>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
