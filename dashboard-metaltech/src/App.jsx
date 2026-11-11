import Dashboard from './pages/Dashboard.jsx'

function App() {
  return (
    <div>
      <header className="cabecalho-metaltech py-3 mb-4">
        <div className="container">
          <h1 className="h3 mb-0">MetalTech Monitoramento Industrial</h1>
        </div>
      </header>

      <main className="container pb-4">
        <Dashboard />
      </main>
    </div>
  )
}

export default App