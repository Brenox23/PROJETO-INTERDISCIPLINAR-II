function CartaoIndicador({ titulo, valor, unidade, cor, icone }) {
  return (
    <div className={`card shadow-sm border-0 border-start border-4 border-${cor} h-100`}>
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center">
          <div>
            <p className="text-muted mb-1 small text-uppercase">{titulo}</p>
            <h2 className="mb-0">
              {valor}
              <span className="fs-5 text-muted">{unidade}</span>
            </h2>
          </div>
          <span className="fs-1">{icone}</span>
        </div>
      </div>
    </div>
  )
}

export default CartaoIndicador