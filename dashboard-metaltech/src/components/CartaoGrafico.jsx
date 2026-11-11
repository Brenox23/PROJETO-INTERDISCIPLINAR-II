function CartaoGrafico({ titulo, children }) {
  return (
    <div className="card shadow-sm border-0 h-100">
      <div className="card-body">
        <h2 className="h6 text-muted text-uppercase mb-3">{titulo}</h2>
        <div style={{ width: '100%', height: 300 }}>{children}</div>
      </div>
    </div>
  )
}

export default CartaoGrafico
