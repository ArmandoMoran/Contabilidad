package com.contabilidad.catalogs;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogs")
public class CatalogController {

    private final SatRegimenFiscalRepository regimenFiscalRepo;
    private final SatUsoCfdiRepository usoCfdiRepo;
    private final SatFormaPagoRepository formaPagoRepo;
    private final SatClaveProdServRepository claveProdServRepo;
    private final SatClaveUnidadRepository claveUnidadRepo;
    private final SatObjetoImpRepository objetoImpRepo;
    private final SatImpuestoRepository impuestoRepo;
    private final SatTipoFactorRepository tipoFactorRepo;
    private final SatTasaOCuotaRepository tasaOCuotaRepo;
    private final SatMotivoCancelacionRepository motivoCancelacionRepo;

    public CatalogController(SatRegimenFiscalRepository regimenFiscalRepo,
                             SatUsoCfdiRepository usoCfdiRepo,
                             SatFormaPagoRepository formaPagoRepo,
                             SatClaveProdServRepository claveProdServRepo,
                             SatClaveUnidadRepository claveUnidadRepo,
                             SatObjetoImpRepository objetoImpRepo,
                             SatImpuestoRepository impuestoRepo,
                             SatTipoFactorRepository tipoFactorRepo,
                             SatTasaOCuotaRepository tasaOCuotaRepo,
                             SatMotivoCancelacionRepository motivoCancelacionRepo) {
        this.regimenFiscalRepo = regimenFiscalRepo;
        this.usoCfdiRepo = usoCfdiRepo;
        this.formaPagoRepo = formaPagoRepo;
        this.claveProdServRepo = claveProdServRepo;
        this.claveUnidadRepo = claveUnidadRepo;
        this.objetoImpRepo = objetoImpRepo;
        this.impuestoRepo = impuestoRepo;
        this.tipoFactorRepo = tipoFactorRepo;
        this.tasaOCuotaRepo = tasaOCuotaRepo;
        this.motivoCancelacionRepo = motivoCancelacionRepo;
    }

    @GetMapping("/{catalogName}")
    public List<?> getCatalog(@PathVariable String catalogName) {
        return switch (catalogName) {
            case "regimen-fiscal" -> regimenFiscalRepo.findAllByActiveTrue();
            case "uso-cfdi" -> usoCfdiRepo.findAllByActiveTrue();
            case "forma-pago" -> formaPagoRepo.findAllByActiveTrue();
            case "clave-prod-serv" -> claveProdServRepo.findAllByActiveTrue();
            case "clave-unidad" -> claveUnidadRepo.findAllByActiveTrue();
            case "objeto-imp" -> objetoImpRepo.findAllByActiveTrue();
            case "impuesto" -> impuestoRepo.findAllByActiveTrue();
            case "tipo-factor" -> tipoFactorRepo.findAllByActiveTrue();
            case "tasa-o-cuota" -> tasaOCuotaRepo.findAllByActiveTrue();
            case "motivo-cancelacion" -> motivoCancelacionRepo.findAllByActiveTrue();
            default -> throw new IllegalArgumentException("Unknown catalog: " + catalogName);
        };
    }
}
