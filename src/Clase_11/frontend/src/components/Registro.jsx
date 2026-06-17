import React, {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';

export default function Registro() {
    const [nombre, setNombre] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [exito, setExito] = useState('');
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError('');
        setExito('');
        setLoading(true);

        try {
            const res = await fetch('/api/auth/registro', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({nombre, email, password}),
            });

            if (res.status === 201) {
                setExito('Cuenta creada. Redirigiendo...');
                setTimeout(() => navigate('/login'), 1500);
            } else if (res.status === 409) {
                setError('El email ya está registrado');
            } else if (res.status === 400) {
                setError('Por favor verifica los datos ingresados');
            } else {
                setError('Error al registrar la cuenta');
            }
        } catch {
            setError('Error al conectar con el servidor');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="min-h-screen bg-[#EBF5FB] flex items-center justify-center">
            <div className="bg-white rounded-lg shadow-md p-8 w-full max-w-sm">
                <h1 className="text-2xl font-bold text-gray-700 mb-6 text-center">
                    Registro
                </h1>

                <form onSubmit={handleSubmit}>
                    {/* Nombre */}
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-600" htmlFor="nombre">
                            Nombre
                        </label>
                        <input
                            id="nombre"
                            type="text"
                            placeholder="Ingresa tu nombre"
                            value={nombre}
                            onChange={(e) => setNombre(e.target.value)}
                            required
                            minLength={2}
                            className="w-full border border-gray-300 rounded px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-[#2E86C1]"
                        />
                    </div>

                    {/* Email */}
                    <div className="mb-4">
                        <label className="block text-sm font-medium text-gray-600" htmlFor="email">
                            Email
                        </label>
                        <input
                            id="email"
                            type="email"
                            placeholder="Ingresa tu email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="w-full border border-gray-300 rounded px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-[#2E86C1]"
                        />
                    </div>

                    {/* Contraseña */}
                    <div className="mb-2">
                        <label className="block text-sm font-medium text-gray-600" htmlFor="password">
                            Contraseña
                        </label>
                        <input
                            id="password"
                            type="password"
                            placeholder="Crea una contraseña"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            minLength={6}
                            className="w-full border border-gray-300 rounded px-3 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-[#2E86C1]"
                        />
                    </div>

                    {/* Mensajes de error / éxito */}
                    {error && (
                        <p className="text-red-500 text-sm mt-2">{error}</p>
                    )}
                    {exito && (
                        <p className="text-green-600 text-sm mt-2">{exito}</p>
                    )}

                    {/* Botón submit */}
                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-[#27AE60] hover:bg-[#1E8449] text-white font-semibold py-2 rounded mt-4 disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
                    >
                        {loading ? 'Registrando...' : 'Registrarse'}
                    </button>
                </form>

                {/* Link a login */}
                <p className="text-center text-sm text-gray-600 mt-4">
                    ¿Ya tienes una cuenta?{' '}
                    <Link to="/login" className="text-[#2E86C1] hover:underline font-medium">
                        Inicia sesión
                    </Link>
                </p>
            </div>
        </div>
    );
}
