import React, {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import {useAuth} from '../contexts/AuthContext';

export default function Navbar() {
    // 1. Unificamos todo lo que extraes de useAuth en una sola línea
    const {token, user, logout} = useAuth();
    const navigate = useNavigate();

    // 2. Corregimos el estado para que coincida con lo que usas después
    const [nombre, setNombre] = useState('');

    useEffect(() => {
        async function cargarPerfil() {
            try {
                // Hacemos la petición a la API
                const res = await fetch('/api/usuario/perfil', {
                    headers: {Authorization: `Bearer ${token}`},
                });

                if (res.ok) {
                    const data = await res.json();
                    setNombre(data.nombre);
                }
            } catch (err) {
                console.error('Error al cargar perfil:', err);
            }
        }

        if (token) {
            cargarPerfil();
        }
    }, [token]);

    function handleLogout() {
        logout();
        navigate('/login');
    }

    return (
        <nav className="bg-[#2E86C1] text-white px-6 py-3 flex items-center justify-between shadow">
            {/* Izquierda: bienvenida */}
            <span className="font-semibold text-base">
                Bienvenido, {nombre}
            </span>

            {/* Derecha: navegación */}
            <div className="flex items-center gap-5">
                <Link to="/mis-cursos" className="hover:underline text-sm font-medium">
                    Mis Cursos
                </Link>
                <Link to="/perfil" className="hover:underline text-sm font-medium">
                    Mi Perfil
                </Link>
                {user?.isAdmin === true && (
                    <Link to="/admin" className="hover:underline text-sm font-medium">
                        Admin Panel
                    </Link>
                )}
                <button onClick={handleLogout} className="hover:underline text-sm font-medium">
                    Cerrar Sesión
                </button>
            </div>
        </nav>
    );
}