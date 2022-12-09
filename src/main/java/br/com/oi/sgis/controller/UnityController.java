package br.com.oi.sgis.controller;

import br.com.oi.sgis.dto.*;
import br.com.oi.sgis.enums.InstallationReasonEnum;
import br.com.oi.sgis.enums.ReasonForWriteOffEnum;
import br.com.oi.sgis.exception.NotReprocessableUnityException;
import br.com.oi.sgis.exception.UnityException;
import br.com.oi.sgis.exception.UnityNotFoundException;
import br.com.oi.sgis.service.CompositionService;
import br.com.oi.sgis.service.UnityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/unities")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UnityController {

    private final UnityService unityService;
    private final CompositionService compositionService;

    @PostMapping("/cus")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createNewSpareUnity(@Valid @RequestBody UnityDTO unityDTO){
        return unityService.createNewSpareUnity(unityDTO);
    }

    @PostMapping("/crp")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDTO createUnityRemovedFromSite(@Valid @RequestBody UnityDTO unityDTO){
        return unityService.createRemovedFromSiteUnity(unityDTO);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateUnity(@Valid @RequestBody UnityDTO unityDTO){
        return unityService.updateUnity(unityDTO);
    }

    @GetMapping("/{id}")
    public UnityDTO findById(@PathVariable String id) {
            return unityService.findById(id);
    }


    @GetMapping("/{id}/installation-transfer")
    public InstallationTransferDTO getInstallationTransfer(@PathVariable String id) {
        return unityService.getInstallationTransfer(id);
    }

    @GetMapping("/{id}/warranties")
    public List<WarrantyViewDTO> getWarranty(@PathVariable String id) throws UnityNotFoundException {
        return unityService.getWarranty(id);
    }

    @GetMapping("/{id}/repair-tickets")
    public List<RepairTicketDTO> getTickets(@PathVariable String id) throws UnityNotFoundException {
        return unityService.getTickets(id);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>  listAllWithSearch(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                                    @RequestParam (required = false) List<String> sortAsc,
                                                                    @RequestParam (required = false, defaultValue = "registerDate") List<String> sortDesc,
                                                                    @RequestParam(defaultValue = "", required = false, value = "search") String term){

        PaginateResponseDTO<Object> unities =  unityService.searchByTermsPaginated(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(unities, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/property-transfer")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>listAllPropertyTransf(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                                                          @RequestParam (required = false) List<String> sortAsc,
                                                                          @RequestParam (required = false, defaultValue = "registerDate") List<String> sortDesc,
                                                                          @RequestParam(defaultValue = "", required = false, value = "search") String term){

        PaginateResponseDTO<Object> unities =  unityService.listUnitiesForPropertyTransf(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(unities, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/property-transfer")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updatePropertyTransf(@Valid @RequestBody UnityDTO unityDTO) {
        return unityService.updatePropertyTransf(unityDTO);
    }

    @GetMapping("/property-resumption")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>listAllPropertyRepos(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                                            @RequestParam (required = false) List<String> sortAsc,
                                                                            @RequestParam (required = false, defaultValue = "registerDate") List<String> sortDesc,
                                                                            @RequestParam(defaultValue = "", required = false, value = "search") String term){

        PaginateResponseDTO<Object> unities =  unityService.listUnitiesForPropertyRepos(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(unities, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/property-resumption")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updatePropertyRepos(@Valid @RequestBody UnityDTO unityDTO) {
        return unityService.updatePropertyRepo(unityDTO);
    }

    @GetMapping("/change-situation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>listAllUpdateItemSituation(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                                            @RequestParam (required = false) List<String> sortAsc,
                                                                            @RequestParam (required = false, defaultValue = "registerDate") List<String> sortDesc,
                                                                            @RequestParam(defaultValue = "", required = false, value = "search") String term){

        PaginateResponseDTO<Object> unities =  unityService.listUnitiesForUpdateSituation(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(unities, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/change-situation")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateUnitySituation(@Valid @RequestBody UnitySituationDTO unitySituationDTO){
        return unityService.updateUnitySituation(unitySituationDTO);
    }

    @GetMapping("/swap-item")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>listAllUpdateItemSwap(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                 @RequestParam (required = false) List<String> sortAsc,
                                                                                 @RequestParam (required = false, defaultValue = "registerDate") List<String> sortDesc,
                                                                                 @RequestParam(defaultValue = "", required = false, value = "search") String term){

        PaginateResponseDTO<Object> unities =  unityService.listUnitiesForUnitySwap(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(unities, new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/swap-item")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO updateUnitySwap(@Valid @RequestBody UnitySwapDTO unitySwapDTO){
        return unityService.updateUnitySwap(unitySwapDTO);
    }

    @GetMapping("/plan-installation")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>listAllToPlanInstallation(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                                                            @RequestParam (required = false) List<String> sortAsc,
                                                                            @RequestParam (required = false, defaultValue = "registerDate") List<String> sortDesc,
                                                                            @RequestParam(defaultValue = "", required = false, value = "search") String term){

        PaginateResponseDTO<Object> unities =  unityService.listUnitiesToPlanInstallation(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(unities, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/plan-installation/reasons")
    public List<InstallationReasonDTO> installationReasons(){
        return Arrays.stream(InstallationReasonEnum.values()).map(
                        reason->
                                InstallationReasonDTO.builder().cod(reason.getCod()).description(reason.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PutMapping("/plan-installation")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO planInstallation(@Valid @RequestBody PlanInstallationDTO planInstallationDTO) throws UnityNotFoundException {
        return unityService.updatePlanInstallation(planInstallationDTO);
    }

    @GetMapping("/write-off/reasons")
    public List<ReasonForWriteOffDTO> writeOffReasons(){
        return Arrays.stream(ReasonForWriteOffEnum.values()).map(
                        reason->
                                ReasonForWriteOffDTO.builder().cod(reason.getCod()).description(reason.getDescription()).build())
                .collect(Collectors.toList());
    }

    @PutMapping("/write-off")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO writeOffUnity(@Valid @RequestBody UnityWriteOffDTO unityWriteOffDTO){
        return unityService.updateUnityWriteOff(unityWriteOffDTO);
    }

    @PutMapping("/bo-number")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO registerBoNumber(@Valid @RequestBody RegisterBoDTO registerBoDTO) throws UnityNotFoundException {
        return unityService.registerBoNumber(registerBoDTO);
    }

    @PutMapping("/recover-item")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO recoverItem(@Valid @RequestBody RecoverItemDTO recoverItemDTO) {
        return unityService.recoverItem(recoverItemDTO);
    }

    @GetMapping("/recover-item")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PaginateResponseDTO<Object>>listAllToRecoverItem(@RequestParam(defaultValue = "1") Integer pageNo,
                                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                                @RequestParam (required = false) List<String> sortAsc,
                                                                                @RequestParam (required = false, defaultValue = "registerDate") List<String> sortDesc,
                                                                                @RequestParam(defaultValue = "", required = false, value = "search") String term){

        PaginateResponseDTO<Object> unities =  unityService.listUnitiesForItemRecover(pageNo, pageSize, sortAsc, sortDesc,term);
        return new ResponseEntity<>(unities, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/sap-reprocess/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UnityToReprocessSapDTO sapReprocess(@PathVariable String id) throws NotReprocessableUnityException {
        return unityService.sapUnityReprocessable(id);
    }

    @PutMapping("/sap-reprocess")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO sapReprocess(@Valid @RequestBody UnityToReprocessSapDTO unityToReprocessSapDTO) {
        return unityService.reprocessSapUnity(unityToReprocessSapDTO);
    }

    @PutMapping("/general-acceptance")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO generalAcceptance(@Valid @RequestBody UnityAcceptanceDTO unityAcceptanceDTO) throws UnityNotFoundException, UnityException {
        return unityService.generalAcceptance(unityAcceptanceDTO);
    }

    @GetMapping("/composition/{modelId}")
    @ResponseStatus(HttpStatus.OK)
    public CompositionUnityDTO composition(@PathVariable String modelId)  {
        return compositionService.getComposition(modelId);
    }

    @PutMapping("/composition/{modelId}/add-item/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public CompositionUnityDTO addComposition(@PathVariable String modelId, @PathVariable String itemId )  {
        return compositionService.addComposition(modelId, itemId);
    }

    @PutMapping("/composition/remove-item")
    @ResponseStatus(HttpStatus.OK)
    public CompositionUnityDTO removeComposition(@Valid @RequestBody CompositionUnityDTO compositionUnityDTO)  {
        return compositionService.removeComposition(compositionUnityDTO);
    }

    @PostMapping("/composition/report")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> compositionReport(@Valid @RequestBody CompositionUnityDTO compositionUnityDTO) {
        byte[] report = compositionService.report(compositionUnityDTO);
        String filename ="Relatorio_Composicao.pdf";
        return getBodyResponse(report, filename);
    }

    private ResponseEntity<byte[]> getBodyResponse(byte[] report, String filename) {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)//optional
                .body(report);
    }

    @PutMapping("/change-barcode")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponseDTO changeBarcode(@Valid @RequestBody ChangeBarcodeDTO changeBarcode)  {
        return unityService.changeBarcode(changeBarcode);
    }

    @PostMapping("/tracking-record")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> trackingRecordReport(@RequestBody  List<UnityDTO> unities) {
        byte[] report = unityService.trackingRecord(unities);
        String filename ="Ficha_Acompanhamento.pdf";
        return getBodyResponse(report, filename);
    }

    @PostMapping("/extraction")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> unitExtractionReport(@RequestBody  UnitExtractionDTO unitExtractionDTO) {
        byte[] report = unityService.unitExtractionReport(unitExtractionDTO);
        String filename ="Extracao_Unidade.xlsx";
        return  ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(report);
    }
}
