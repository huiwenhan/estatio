package org.incode.module.document.dom.impl.docs;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.applicability.Applicability;
import org.incode.module.document.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisorAttachToNone;
import org.incode.module.document.dom.impl.applicability.RendererModelFactory;
import org.incode.module.document.dom.impl.renderers.Renderer;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToBytes;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToBytesWithPreviewToUrl;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToChars;
import org.incode.module.document.dom.impl.renderers.RendererFromBytesToCharsWithPreviewToUrl;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToBytes;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToBytesWithPreviewToUrl;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToChars;
import org.incode.module.document.dom.impl.renderers.RendererFromCharsToCharsWithPreviewToUrl;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.services.ClassNameViewModel;
import org.incode.module.document.dom.services.ClassService;
import org.incode.module.document.dom.spi.AttachmentAdvisorClassNameService;
import org.incode.module.document.dom.spi.RendererModelFactoryClassNameService;
import org.incode.module.document.dom.types.AtPathType;
import org.incode.module.document.dom.types.FqcnType;

import org.estatio.module.invoice.dom.DocumentTemplateData;
import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.invoice.dom.RenderingStrategyData;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema = "incodeDocuments"
)
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @javax.jdo.annotations.Query(
                name = "findByTypeDataAndAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeDataCopy   == :typeData "
                        + "   && atPathCopy == :atPath "
                        + "ORDER BY date DESC"
        ),
        @javax.jdo.annotations.Query(
                name = "findByTypeDataAndAtPathAndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeDataCopy   == :typeData "
                        + "   && atPathCopy == :atPath "
                        + "   && date       == :date "
        ),
        @javax.jdo.annotations.Query(
                name = "findByTypeDataAndApplicableToAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeDataCopy   == :typeData "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "ORDER BY atPathCopy DESC, date DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByApplicableToAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE :atPath.startsWith(atPathCopy) "
                        + "ORDER BY typeCopy ASC, atPathCopy DESC, date DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "ORDER BY atPathCopy DESC, date DESC "
        )
})
@Uniques({
        @Unique(
                name = "DocumentTemplate_type_atPath_date_IDX",
                members = { "typeCopy", "atPathCopy", "date" }
        ),
        @Unique(
                name = "DocumentTemplate_typeData_atPath_date_IDX",
                members = { "typeDataCopy", "atPathCopy", "date" }
        ),
})
@Indices({
        @Index(
                name = "DocumentTemplate_atPath_date_IDX",
                members = { "atPathCopy", "date" }
        ),
        @Index(
                name = "DocumentTemplate_type_date_IDX",
                members = { "typeCopy", "date" }
        ),
        @Index(
                name = "DocumentTemplate_type_date_IDX",
                members = { "typeDataCopy", "date" }
        ),
})
@DomainObject(
        objectType = "incodeDocuments.DocumentTemplate",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class DocumentTemplate
        extends DocumentAbstract<DocumentTemplate> {


    //region > title, icon, cssClass
    public TranslatableString title() {
        if(this.getDate() != null) {
            return TranslatableString.tr("[{type}] ({date})",
                    "type", this.getType().getReference(),
                    "date", this.getDate());
        } else {
            return TranslatableString.tr("[{type}] {name}",
                    "name", this.getName(),
                    "type", this.getType().getReference());
        }
    }


    //region > constructor

    @NotPersistent
    @Getter
    private final DocumentTemplateData templateData;


    DocumentTemplate() {
        // for unit testing only
        this.templateData = null;
    }

    public DocumentTemplate(
            final DocumentType type,
            final DocumentTypeData typeData,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Blob blob,
            final String subjectText) {
        this(type, typeData, date, atPath, fileSuffix, previewOnly, subjectText);
        modifyBlob(blob);
    }

    public DocumentTemplate(
            final DocumentType type,
            final DocumentTypeData typeData,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final String name, final String mimeType, final String text,
            final String subjectText) {
        this(type, typeData, date, atPath, fileSuffix, previewOnly, subjectText);
        setTextData(name, mimeType, text);
    }

    public DocumentTemplate(
            final DocumentType type,
            final DocumentTypeData typeData,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final Clob clob,
            final String subjectText) {
        this(type, typeData, date, atPath, fileSuffix, previewOnly, subjectText);
        modifyClob(clob);
    }

    private DocumentTemplate(
            final DocumentType type,
            final DocumentTypeData typeData,
            final LocalDate date,
            final String atPath,
            final String fileSuffix,
            final boolean previewOnly,
            final String nameText) {
        super(type, typeData, atPath);

        this.typeCopy = type;
        this.typeDataCopy = typeData;
        this.atPathCopy = atPath;

        this.templateData = typeData.lookup(atPath);
        this.date = date;
        this.fileSuffix = stripLeadingDotAndLowerCase(fileSuffix);
        this.previewOnly = previewOnly;
        this.nameText = nameText;
    }

    static String stripLeadingDotAndLowerCase(final String fileSuffix) {
        final int lastDot = fileSuffix.lastIndexOf(".");
        final String stripLeadingDot = fileSuffix.substring(lastDot+1);
        return stripLeadingDot.toLowerCase();
    }

    //endregion


    /**
     * Copy of {@link #getType()}, for query purposes only.
     */
    @Getter @Setter
    @Column(allowsNull = "false", name = "typeId")
    @Property(
            notPersisted = true, // ignore for auditing
            hidden = Where.EVERYWHERE
    )
    private DocumentType typeCopy;

    /**
     * Copy of {@link #getTypeData()}, for query purposes only.
     */
    @Getter @Setter
    @Column(allowsNull = "false", length = DocumentType.ReferenceType.Meta.MAX_LEN)
    @Property(
            notPersisted = true,
            hidden = Where.EVERYWHERE
    )
    private DocumentTypeData typeDataCopy;

    /**
     * Copy of {@link #getAtPath()}, for query purposes only.
     */
    @Getter @Setter
    @Column(allowsNull = "false", length = AtPathType.Meta.MAX_LEN)
    @Property(
            notPersisted = true, // ignore for auditing
            hidden = Where.EVERYWHERE
    )
    private String atPathCopy;

    @Programmatic
    public RenderingStrategyData getContentRenderingStrategyData() {
        return templateData.getContentRenderingStrategy();
    }

    @Programmatic
    public RenderingStrategyData getNameRenderingStrategyData() {
        return templateData.getNameRenderingStrategy();
    }


    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(editing = Editing.DISABLED)
    private LocalDate date;

    @Getter @Setter
    @Column(allowsNull = "false", length = FileSuffixType.Meta.MAX_LEN)
    @Property(editing = Editing.DISABLED)
    private String fileSuffix;

    /**
     * Used to determine the name of the {@link Document#getName() name} of the rendered {@link Document}.
     */
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false", length = NameTextType.Meta.MAX_LEN)
    @Property(
            notPersisted = true, // exclude from auditing
            editing = Editing.DISABLED
    )
    private String nameText;

    /**
     * Whether this template can only be previewed (not used to also create a document).
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(editing = Editing.DISABLED)
    private boolean previewOnly;


    @javax.jdo.annotations.Persistent(mappedBy = "documentTemplate", dependentElement = "true")
    @Collection(editing = Editing.DISABLED)
    @Getter @Setter
    private SortedSet<Applicability> appliesTo = new TreeSet<>();


    //region > applicable (action)

    /**
     * TODO: remove once moved over to using DocumentTypeData and DocumentTemplateData
     */
    @Mixin
    public static class _applicable {
        private final DocumentTemplate documentTemplate;

        public _applicable(final DocumentTemplate documentTemplate) {
            this.documentTemplate = documentTemplate;
        }


        public static class ActionDomainEvent extends DocumentAbstract.ActionDomainEvent { }

        @Action(domainEvent = ActionDomainEvent.class, semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(cssClassFa = "fa-plus", contributed = Contributed.AS_ACTION)
        @MemberOrder(name = "appliesTo", sequence = "1")
        public DocumentTemplate $$(
                @Parameter(maxLength = FqcnType.Meta.MAX_LEN, mustSatisfy = FqcnType.Meta.Specification.class)
                @ParameterLayout(named = "Domain type")
                final String domainClassName,
                @Parameter(maxLength = FqcnType.Meta.MAX_LEN, mustSatisfy = FqcnType.Meta.Specification.class)
                @ParameterLayout(named = "Renderer Model Factory")
                final ClassNameViewModel rendererModelFactoryClassNameViewModel,
                @Parameter(maxLength = FqcnType.Meta.MAX_LEN, mustSatisfy = FqcnType.Meta.Specification.class)
                @ParameterLayout(named = "Attachment Advisor")
                final ClassNameViewModel attachmentAdvisorClassNameViewModel) {

            applicable(
                    domainClassName, rendererModelFactoryClassNameViewModel.getFullyQualifiedClassName(), attachmentAdvisorClassNameViewModel.getFullyQualifiedClassName());
            return this.documentTemplate;
        }

        public TranslatableString disable$$() {
            if (rendererModelFactoryClassNameService == null) {
                return TranslatableString.tr(
                        "No RendererModelFactoryClassNameService registered to locate implementations of RendererModelFactory");
            }
            if (attachmentAdvisorClassNameService == null) {
                return TranslatableString.tr(
                        "No AttachmentAdvisorClassNameService registered to locate implementations of AttachmentAdvisor");
            }
            return null;
        }

        public List<ClassNameViewModel> choices1$$() {
            return rendererModelFactoryClassNameService.rendererModelFactoryClassNames();
        }

        public List<ClassNameViewModel> choices2$$() {
            return attachmentAdvisorClassNameService.attachmentAdvisorClassNames();
        }

        public TranslatableString validate0$$(final String domainTypeName) {

            return isApplicable(domainTypeName) ?
                    TranslatableString.tr(
                            "Already applicable for '{domainTypeName}'",
                            "domainTypeName", domainTypeName)
                    : null;
        }


        @Programmatic
        public Applicability applicable(
                final Class<?> domainClass,
                final Class<? extends RendererModelFactory> renderModelFactoryClass,
                final Class<? extends AttachmentAdvisor> attachmentAdvisorClass) {
            return applicable(
                    domainClass.getName(),
                    renderModelFactoryClass,
                    attachmentAdvisorClass != null
                            ? attachmentAdvisorClass
                            : AttachmentAdvisorAttachToNone.class
            );
        }

        @Programmatic
        public Applicability applicable(
                final String domainClassName,
                final Class<? extends RendererModelFactory> renderModelFactoryClass,
                final Class<? extends AttachmentAdvisor> attachmentAdvisorClass) {
            return applicable(domainClassName, renderModelFactoryClass.getName(), attachmentAdvisorClass.getName() );
        }

        @Programmatic
        public Applicability applicable(
                final String domainClassName,
                final String renderModelFactoryClassName,
                final String attachmentAdvisorClassName) {
            Applicability applicability = existingApplicability(domainClassName);
            if(applicability == null) {
                applicability = applicabilityRepository.create(documentTemplate, domainClassName, renderModelFactoryClassName, attachmentAdvisorClassName);
            } else {
                applicability.setRendererModelFactoryClassName(renderModelFactoryClassName);
                applicability.setAttachmentAdvisorClassName(attachmentAdvisorClassName);
            }
            return applicability;
        }

        private boolean isApplicable(final String domainClassName) {
            return existingApplicability(domainClassName) != null;
        }
        private Applicability existingApplicability(final String domainClassName) {
            SortedSet<Applicability> applicabilities = documentTemplate.getAppliesTo();
            for (Applicability applicability : applicabilities) {
                if (applicability.getDomainClassName().equals(domainClassName)) {
                    return applicability;
                }
            }
            return null;
        }

        @Inject
        RendererModelFactoryClassNameService rendererModelFactoryClassNameService;
        @Inject
        AttachmentAdvisorClassNameService attachmentAdvisorClassNameService;
        @Inject
        ApplicabilityRepository applicabilityRepository;
    }
    //endregion


    //region > appliesTo, newRendererModelFactory + newRendererModel, newAttachmentAdvisor + newAttachmentAdvice

    private RendererModelFactory newRendererModelFactory(final Object domainObject) {
        final Class<?> domainClass = domainObject.getClass();
        return getTemplateData().newRenderModelFactory(domainClass, classService, serviceRegistry2);
    }

    @Programmatic
    public AttachmentAdvisor newAttachmentAdvisor(final Object domainObject) {
        final Class<?> domainClass = domainObject.getClass();
        return getTemplateData().newAttachmentAdvisor(domainClass, classService, serviceRegistry2);
    }


    @Programmatic
    public Object newRendererModel(final Object domainObject) {
        final RendererModelFactory rendererModelFactory = newRendererModelFactory(domainObject);
        if(rendererModelFactory == null) {
            throw new IllegalStateException(String.format(
                    "For domain template %s, could not locate Applicability for domain object: %s",
                    getName(), domainObject.getClass().getName()));
        }
        final Object rendererModel = rendererModelFactory.newRendererModel(this, domainObject);
        serviceRegistry2.injectServicesInto(rendererModel);
        return rendererModel;
    }

    @Programmatic
    public List<AttachmentAdvisor.PaperclipSpec> newAttachmentAdvice(final Document document, final Object domainObject) {
        final AttachmentAdvisor attachmentAdvisor = newAttachmentAdvisor(domainObject);
        if(attachmentAdvisor == null) {
            throw new IllegalStateException(String.format(
                    "For domain template %s, could not locate Applicability for domain object: %s",
                    getName(), domainObject.getClass().getName()));
        }
        final List<AttachmentAdvisor.PaperclipSpec> paperclipSpecs = attachmentAdvisor.advise(this, domainObject,
                document);
        return paperclipSpecs;
    }
    //endregion



    //region > preview, previewUrl (programmatic)

    @Programmatic
    public URL previewUrl(final Object rendererModel) throws IOException {

        serviceRegistry2.injectServicesInto(rendererModel);

        if(!templateData.getContentRenderingStrategy().isPreviewsToUrl()) {
            throw new IllegalStateException(String.format("RenderingStrategy '%s' does not support previewing to URL",
                    templateData.getContentRenderingStrategy().getReference()));
        }

        final DocumentNature inputNature = templateData.getContentRenderingStrategy().getInputNature();
        final DocumentNature outputNature = templateData.getContentRenderingStrategy().getOutputNature();

        final Renderer renderer = getContentRenderingStrategyData().newRenderer(
                classService, serviceRegistry2);
        switch (inputNature){
        case BYTES:
            switch (outputNature) {
            case BYTES:
                return ((RendererFromBytesToBytesWithPreviewToUrl) renderer).previewBytesToBytes(
                        getType(), getAtPath(), getVersion(),
                        asBytes(), rendererModel);
            case CHARACTERS:
                return ((RendererFromBytesToCharsWithPreviewToUrl) renderer).previewBytesToChars(
                        getType(), getAtPath(), getVersion(),
                        asBytes(), rendererModel);
            default:
                // shouldn't happen, above switch statement is complete
                throw new IllegalArgumentException(String.format("Unknown output DocumentNature '%s'", outputNature));
            }
        case CHARACTERS:
            switch (outputNature) {
            case BYTES:
                return ((RendererFromCharsToBytesWithPreviewToUrl) renderer).previewCharsToBytes(
                        getType(), getAtPath(), getVersion(),
                        asChars(), rendererModel);
            case CHARACTERS:
                return ((RendererFromCharsToCharsWithPreviewToUrl) renderer).previewCharsToChars(
                        getType(), getAtPath(), getVersion(),
                        asChars(), rendererModel);
            default:
                // shouldn't happen, above switch statement is complete
                throw new IllegalArgumentException(String.format("Unknown output DocumentNature '%s'", outputNature));
            }

        default:
            // shouldn't happen, above switch statement is complete
            throw new IllegalArgumentException(String.format("Unknown input DocumentNature '%s'", inputNature));
        }
    }

    //endregion

    //region > create, createAndRender, createAndScheduleRender (programmatic)

    @Programmatic
    public Document create(final Object domainObject) {
        final Document document = createDocumentUsingRendererModel(domainObject);
        transactionService.flushTransaction();
        return document;
    }

    @Programmatic
    public Document createAndScheduleRender(final Object domainObject) {
        final Document document = create(domainObject);
        backgroundService2.execute(document).render(this, domainObject);
        return document;
    }
    @Programmatic
    public Document createAndRender(final Object domainObject) {
        final Document document = create(domainObject);
        document.render(this, domainObject);
        return document;
    }
    //endregion

    //region > createDocument (programmatic)
    @Programmatic
    public Document createDocumentUsingRendererModel(
            final Object domainObject) {
        final Object rendererModel = newRendererModel(domainObject);
        final String documentName = determineDocumentName(rendererModel);
        return createDocument(documentName);
    }

    private String determineDocumentName(final Object contentDataModel) {

        serviceRegistry2.injectServicesInto(contentDataModel);

        // subject
        final RendererFromCharsToChars nameRenderer =
                (RendererFromCharsToChars) getNameRenderingStrategyData().newRenderer(
                        classService, serviceRegistry2);
        String renderedDocumentName;
        try {
            renderedDocumentName = nameRenderer.renderCharsToChars(
                    getType(), "name", getAtPath(), getVersion(),
                    getNameText(), contentDataModel);
        } catch (IOException e) {
            renderedDocumentName = getName();
        }
        return withFileSuffix(renderedDocumentName);
    }

    private Document createDocument(String documentName) {
        return documentRepository.create(getType(), getTypeData(), getAtPath(), documentName, getMimeType());
    }
    //endregion

    //region > renderContent (programmatic)
    @Programmatic
    public void renderContent(
            final Document document,
            final Object contentDataModel) {
        renderContent((DocumentLike)document, contentDataModel);
    }

    @Programmatic
    public void renderContent(
            final DocumentLike document,
            final Object contentDataModel) {
        final String documentName = determineDocumentName(contentDataModel);
        document.setName(documentName);
        final RenderingStrategyData renderingStrategy = getContentRenderingStrategyData();
        final String variant = "content";
        try {

            final DocumentNature inputNature = renderingStrategy.getInputNature();
            final DocumentNature outputNature = renderingStrategy.getOutputNature();

            final Renderer renderer = renderingStrategy.newRenderer(classService, serviceRegistry2);

            switch (inputNature){
                case BYTES:
                    switch (outputNature) {
                    case BYTES:
                        final byte[] renderedBytes = ((RendererFromBytesToBytes) renderer).renderBytesToBytes(
                                getType(), variant, getAtPath(), getVersion(),
                                asBytes(), contentDataModel);
                        final Blob blob = new Blob (documentName, getMimeType(), renderedBytes);
                        document.modifyBlob(blob);
                        return;
                    case CHARACTERS:
                        final String renderedChars = ((RendererFromBytesToChars) renderer).renderBytesToChars(
                            getType(), variant, getAtPath(), getVersion(),
                            asBytes(), contentDataModel);
                        if(renderedChars.length() <= TextType.Meta.MAX_LEN) {
                            document.setTextData(getName(), getMimeType(), renderedChars);
                        } else {
                            final Clob clob = new Clob (documentName, getMimeType(), renderedChars);
                            document.modifyClob(clob);
                        }
                        return;
                    default:
                    // shouldn't happen, above switch statement is complete
                    throw new IllegalArgumentException(String.format("Unknown output DocumentNature '%s'", outputNature));
                    }
                case CHARACTERS:
                    switch (outputNature) {
                    case BYTES:
                        final byte[] renderedBytes = ((RendererFromCharsToBytes) renderer).renderCharsToBytes(
                                getType(), variant, getAtPath(), getVersion(),
                                asChars(), contentDataModel);
                        final Blob blob = new Blob (documentName, getMimeType(), renderedBytes);
                        document.modifyBlob(blob);
                        return;
                    case CHARACTERS:
                        final String renderedChars = ((RendererFromCharsToChars) renderer).renderCharsToChars(
                                getType(), variant, getAtPath(), getVersion(),
                                asChars(), contentDataModel);
                        if(renderedChars.length() <= TextType.Meta.MAX_LEN) {
                            document.setTextData(getName(), getMimeType(), renderedChars);
                        } else {
                            final Clob clob = new Clob (documentName, getMimeType(), renderedChars);
                            document.modifyClob(clob);
                        }
                        return;
                    default:
                    // shouldn't happen, above switch statement is complete
                    throw new IllegalArgumentException(String.format("Unknown output DocumentNature '%s'", outputNature));
                    }
                default:
                    // shouldn't happen, above switch statement is complete
                    throw new IllegalArgumentException(String.format("Unknown input DocumentNature '%s'", inputNature));
            }

        } catch (IOException e) {
            throw new ApplicationException("Unable to render document template", e);
        }
    }
    //endregion


    //region > withFileSuffix (programmatic)
    @Programmatic
    public String withFileSuffix(final String documentName) {
        final String suffix = getFileSuffix();
        final int lastPeriod = suffix.lastIndexOf(".");
        final String suffixNoDot = suffix.substring(lastPeriod + 1);
        final String suffixWithDot = "." + suffixNoDot;
        if (documentName.endsWith(suffixWithDot)) {
            return trim(documentName, NameType.Meta.MAX_LEN);
        }
        else {
            return StringUtils.stripEnd(trim(documentName, NameType.Meta.MAX_LEN - suffixWithDot.length()),".") + suffixWithDot;
        }
    }

    private static String trim(final String name, final int length) {
        return name.length() > length ? name.substring(0, length) : name;
    }
    //endregion



    //region > getVersion (programmatic)
    @Programmatic
    private long getVersion() {
        return (Long)JDOHelper.getVersion(this);
    }

    //endregion

    //region > injected services
    @Inject
    ClassService classService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
    @Inject
    TransactionService transactionService;
    @Inject
    BackgroundService2 backgroundService2;

    //endregion

    //region > types

    public static class FileSuffixType {

        private FileSuffixType() {}

        public static class Meta {

            public static final int MAX_LEN = 12;

            private Meta() {}

        }
    }

    public static class NameTextType {

        private NameTextType() {}

        public static class Meta {

            public static final int MAX_LEN = 255;

            private Meta() {}

        }
    }


    //endregion

}
