package org.incode.module.document.fixture;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.rendering.RenderingStrategy;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.invoice.dom.DocumentTypeData;

@Programmatic
public abstract class DocumentTemplateFSAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    /**
     * convenience, as templates and types often created together
     * @param reference
     * @param name
     * @param executionContext
     * @return
     */
    protected DocumentType upsertType(
            String reference,
            String name,
            ExecutionContext executionContext) {

        DocumentType documentType = documentTypeRepository.findByReference(reference);
        if(documentType != null) {
            documentType.setName(name);
        } else {
            documentType = documentTypeRepository.create(reference, name);
        }
        return executionContext.addResult(this, documentType);
    }


    protected DocumentTemplate upsertDocumentTextTemplate(
            final DocumentType documentType,
            final DocumentTypeData typeData,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final String name,
            final String mimeType,
            final String contentText,
            final String nameText,
            final RenderingStrategy nameRenderingStrategy,
            final ExecutionContext executionContext) {

        DocumentTemplate documentTemplate = documentTemplateRepository
                                                    .findByTypeAndAtPathAndDate(documentType, atPath, date);
        if(documentTemplate != null) {
            documentTemplate.setFileSuffix(fileSuffix);
            documentTemplate.setPreviewOnly(previewOnly);
            documentTemplate.setName(name);
            documentTemplate.setMimeType(mimeType);
            documentTemplate.setText(contentText);
            documentTemplate.setNameText(nameText);
            documentTemplate.setNameRenderingStrategy(nameRenderingStrategy);
        } else {
            documentTemplate =
                    documentTemplateRepository.createText(
                            documentType, typeData, date, atPath,
                            fileSuffix, previewOnly,
                            name, mimeType,
                            contentText,
                            nameText, nameRenderingStrategy);
        }
        return executionContext.addResult(this, documentTemplate);
    }

    protected DocumentTemplate upsertDocumentClobTemplate(
            final DocumentType documentType,
            final DocumentTypeData typeData,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Clob clob,
            final String nameText, final RenderingStrategy nameRenderingStrategy,
            ExecutionContext executionContext) {

        DocumentTemplate documentTemplate = documentTemplateRepository
                .findByTypeAndAtPathAndDate(documentType, atPath, date);

        if(documentTemplate != null) {
            documentTemplate.setFileSuffix(fileSuffix);
            documentTemplate.setPreviewOnly(previewOnly);
            documentTemplate.modifyClob(clob);
            documentTemplate.setNameText(nameText);
            documentTemplate.setNameRenderingStrategy(nameRenderingStrategy);
        } else {
            documentTemplate =
                    documentTemplateRepository.createClob(
                            documentType, typeData, date, atPath,
                            fileSuffix, previewOnly, clob,
                            nameText, nameRenderingStrategy);
        }
        return executionContext.addResult(this, documentTemplate);
    }

    protected DocumentTemplate upsertDocumentBlobTemplate(
            final DocumentType documentType,
            final DocumentTypeData typeData,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Blob blob,
            final String nameText,
            final RenderingStrategy nameRenderingStrategy,
            ExecutionContext executionContext) {

        DocumentTemplate documentTemplate = documentTemplateRepository
                .findByTypeAndAtPathAndDate(documentType, atPath, date);
        if(documentTemplate != null) {
            documentTemplate.setFileSuffix(fileSuffix);
            documentTemplate.setPreviewOnly(previewOnly);
            documentTemplate.modifyBlob(blob);
            documentTemplate.setNameText(nameText);
            documentTemplate.setNameRenderingStrategy(nameRenderingStrategy);
        } else {
            documentTemplate =
                    documentTemplateRepository.createBlob(
                            documentType, typeData, date, atPath,
                            fileSuffix, previewOnly, blob,
                            nameText, nameRenderingStrategy);
        }

        return executionContext.addResult(this, documentTemplate);
    }

    protected static String buildTemplateName(
            final DocumentTypeData typeData,
            final DocumentType docType,
            final String templateNameSuffixIfAny) {
        return buildTemplateName(docType, typeData, templateNameSuffixIfAny, null);
    }

    protected static String buildTemplateName(
            final DocumentType docType,
            final DocumentTypeData typeData, final String templateNameSuffixIfAny,
            final String extension) {
        final String name = docType.getName() + (templateNameSuffixIfAny != null ? templateNameSuffixIfAny : "");
        return extension != null
                ? name.endsWith(extension)
                ? name
                : name + extension
                : name;
    }


    @Inject
    protected DocumentTemplateRepository documentTemplateRepository;

    @Inject
    protected DocumentTypeRepository documentTypeRepository;

}
