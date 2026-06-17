import React, {useEffect, useState} from 'react';
import Navbar from './Navbar';
import {useAuth} from '../contexts/AuthContext';

export default function Perfil() {
    const {token, actualizarUsuario} = useAuth();

    // Datos cargados del backend
    const [nombre, setNombre] = useState('');
    const [email, setEmail] = useState('');

    // Campos del formulario
    const [nuevoNombre, setNuevoNombre] = useState('');
    const [passwordActual, setPasswordActual] = useState('');
    const [passwordNueva, setPasswordNueva] = useState('');

    // Estados de UI
    const [loading, setLoading] = useState(true);
    const [guardando, setGuardando] = useState(false);
    const [error, setError] = useState('');
    const [exito, setExito] = useState('');

    // Cargar perfil al montar
    useEffect(() => {
        async function cargarPerfil() {
            try {
                const res = await fetch('/api/usuario/perfil', {
                    headers: {Authorization: `Bearer ${token}`},
                });
                if (res.ok) {
                    const data = await res.json();
                    setNombre(data.nombre);
                    setEmail(data.email);
                }
            } catch (err) {
                console.error('Error al cargar perfil:', err);
            } finally {
                setLoading(false);
            }
        }

        cargarPerfil();
    }, [token]);

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');
        setExito('');
        setGuardando(true);

        // Solo enviar campos que tienen valor
        const body = {};
        if (nuevoNombre.trim()) body.nombre = nuevoNombre.trim();
        if (passwordActual) body.passwordActual = passwordActual;
        if (passwordNueva) body.passwordNueva = passwordNueva;

        try {
            const res = await fetch('/api/usuario/perfil', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(body),
            });

            if (res.ok) {
                setExito('Perfil actualizado correctamente');
                
                // Actualizar local y globalmente
                if (body.nombre) {
                    setNombre(body.nombre);
                    actualizarUsuario({ nombre: body.nombre });
                }

                // Limpiar campos de contraseña y nombre
                setNuevoNombre('');
                setPasswordActual('');
                setPasswordNueva('');
            } else if (res.status === 401) {
                setError('Contraseña actual incorrecta');
            } else if (res.status === 400) {
                setError('Por favor verifica los datos ingresados');
            } else {
                setError('Error al actualizar el perfil');
            }
        } catch (err) {
            setError('Error de conexión. Inténtalo de nuevo.');
        } finally {
            setGuardando(false);
        }
    }

    return (
        <div className="min-h-screen bg-[#EBF5FB]">
            <Navbar/>

            <div className="bg-white rounded-lg shadow-md p-8 max-w-md mx-auto mt-8">
                <h1 className="text-xl font-bold mb-4">Mi Perfil</h1>

                {/* Sección de info actual */}
                {loading ? (
                    <p className="text-gray-500 text-sm">Cargando perfil...</p>
                ) : (
                    <div className="mb-4 space-y-1">
                        <p className="text-sm text-gray-600">
                            <span className="font-medium">Nombre:</span> {nombre}
                        </p>
                        <p className="text-sm text-gray-600">
                            <span className="font-medium">Email:</span> {email}
                        </p>
                    </div>
                )}

                <hr className="my-4"/>

                <h2 className="text-base font-bold mb-4">Editar Perfil</h2>

                <form onSubmit={handleSubmit} className="space-y-4">
                    {/* Nuevo nombre */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Nuevo nombre
                        </label>
                        <input
                            type="text"
                            value={nuevoNombre}
                            onChange={(e) => setNuevoNombre(e.target.value)}
                            placeholder={nombre || 'Tu nombre actual'}
                            minLength={2}
                            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#2E86C1]"
                        />
                    </div>

                    {/* Contraseña actual */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Contraseña actual
                        </label>
                        <input
                            type="password"
                            value={passwordActual}
                            onChange={(e) => setPasswordActual(e.target.value)}
                            placeholder="Contraseña actual"
                            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#2E86C1]"
                        />
                    </div>

                    {/* Nueva contraseña */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Nueva contraseña
                        </label>
                        <input
                            type="password"
                            value={passwordNueva}
                            onChange={(e) => setPasswordNueva(e.target.value)}
                            placeholder="Nueva contraseña"
                            minLength={6}
                            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-[#2E86C1]"
                        />
                    </div>

                    {/* Nota informativa */}
                    <p className="text-xs text-gray-400">
                        Deja los campos de contraseña vacíos si no deseas cambiarla
                    </p>

                    {/* Mensajes de error y éxito */}
                    {error && (
                        <p className="text-sm text-red-600 font-medium">{error}</p>
                    )}
                    {exito && (
                        <p className="text-sm text-green-600 font-medium">{exito}</p>
                    )}

                    {/* Botón submit */}
                    <button
                        type="submit"
                        disabled={guardando}
                        className="w-full bg-[#2E86C1] hover:bg-[#2471A3] text-white font-medium py-2 px-4 rounded transition-colors disabled:opacity-60 disabled:cursor-not-allowed"
                    >
                        {guardando ? 'Guardando...' : 'Guardar cambios'}
                    </button>
                </form>
            </div>
        </div>
    );
}
