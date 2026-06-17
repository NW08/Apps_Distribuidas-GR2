import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './components/Login';
import Registro from './components/Registro';
import Cursos from './components/Cursos';
import MisCursos from './components/MisCursos';
import Perfil from './components/Perfil';
import AdminPanel from './components/AdminPanel';

function RutaProtegida({ children }) {
  const { token } = useAuth();
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

function RutaAdmin({ children }) {
  const { token, usuario } = useAuth();
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  if (!usuario?.isAdmin) {
    return <Navigate to="/cursos" replace />;
  }
  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Rutas públicas */}
          <Route path="/login" element={<Login />} />
          <Route path="/registro" element={<Registro />} />

          {/* Rutas protegidas */}
          <Route
            path="/cursos"
            element={
              <RutaProtegida>
                <Cursos />
              </RutaProtegida>
            }
          />
          <Route
            path="/mis-cursos"
            element={
              <RutaProtegida>
                <MisCursos />
              </RutaProtegida>
            }
          />
          <Route
            path="/perfil"
            element={
              <RutaProtegida>
                <Perfil />
              </RutaProtegida>
            }
          />
          <Route
            path="/admin"
            element={
              <RutaAdmin>
                <AdminPanel />
              </RutaAdmin>
            }
          />

          {/* Ruta raíz y catch-all */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
