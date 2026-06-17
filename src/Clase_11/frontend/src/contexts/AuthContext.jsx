import React, {createContext, useContext, useState} from 'react';

export const AuthContext = createContext(null);

export function AuthProvider({children}) {
    const [token, setToken] = useState(() => {
        return localStorage.getItem('token') || null;
    });

    const [usuario, setUsuario] = useState(() => {
        const stored = localStorage.getItem('usuario');
        try {
            return stored ? JSON.parse(stored) : null;
        } catch {
            return null;
        }
    });

    function login(nuevoToken, nuevoUsuario) {
        setToken(nuevoToken);
        setUsuario(nuevoUsuario);
        localStorage.setItem('token', nuevoToken);
        localStorage.setItem('usuario', JSON.stringify(nuevoUsuario));
    }

    function logout() {
        setToken(null);
        setUsuario(null);
        localStorage.removeItem('token');
        localStorage.removeItem('usuario');
    }

    function actualizarUsuario(datosActualizados) {
        setUsuario(prev => {
            if (!prev) return null;
            const nuevoUsuario = {...prev, ...datosActualizados};
            localStorage.setItem('usuario', JSON.stringify(nuevoUsuario));
            return nuevoUsuario;
        });
    }

    return (
        <AuthContext.Provider value={{usuario, token, login, logout, actualizarUsuario}}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
