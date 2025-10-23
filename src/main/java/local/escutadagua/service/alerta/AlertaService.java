package local.escutadagua.service.alerta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import local.escutadagua.dto.alerta.AlertaResponseDTO;
import local.escutadagua.model.Alerta;
import local.escutadagua.model.Usuario;
import local.escutadagua.model.enums.TipoAlerta;
import local.escutadagua.repository.AlertaRepository;

@ApplicationScoped
public class AlertaService {

    @Inject
    AlertaRepository alertaRepository;

    // ===============================================
    // Métodos Públicos para Criação (Chamados por outros Services)
    // ===============================================

    @Transactional
    public void criarAlertaAltoConsumo(Usuario usuario, String detalhes) {
        criarAlerta(
            usuario, 
            TipoAlerta.ALTO_CONSUMO, 
            "Meta Diária Ultrapassada", 
            detalhes
        );
    }

    @Transactional
    public void criarAlertaConsumoExcessivo(Usuario usuario, String detalhes) {
        criarAlerta(
            usuario, 
            TipoAlerta.BANHO_LONGO, // Exemplo de uso de CHUVEIRO como BANHO_LONGO
            "Consumo Excessivo Detectado", 
            detalhes
        );
    }
    
    // ===============================================
    // Lógica Interna
    // ===============================================
    
    @Transactional
    public void criarAlerta(Usuario usuario, TipoAlerta tipo, String titulo, String mensagem) {
        Alerta alerta = new Alerta();
        alerta.setUsuario(usuario);
        alerta.setTipoAlerta(tipo);
        alerta.setTitulo(titulo);
        alerta.setMensagem(mensagem);
        alerta.setNotificadoEm(LocalDateTime.now());
        alerta.setLido(false); // Novo alerta sempre é não lido

        alertaRepository.persist(alerta);
        System.out.println("✅ Alerta criado: " + titulo + " para o usuário " + usuario.getId());
    }

    public List<AlertaResponseDTO> buscarAlertasPorUsuario(Long idUsuario) {
        List<Alerta> alertas = alertaRepository.list("usuario.id = ?1 order by notificadoEm desc", idUsuario);
        return alertas.stream()
                      .map(AlertaResponseDTO::valueOf)
                      .collect(Collectors.toList());
    }

    /**
     * Marca um alerta específico como lido ou suspenso.
     * @param id ID do alerta
     */
    @Transactional
    public AlertaResponseDTO marcarComoLido(Long id) {
        Alerta alerta = alertaRepository.findById(id);
        
        if (alerta == null) {
            throw new IllegalArgumentException("Alerta não encontrado.");
        }
        
        alerta.setLido(true);
        alertaRepository.persist(alerta);
        
        return AlertaResponseDTO.valueOf(alerta);
    }
}