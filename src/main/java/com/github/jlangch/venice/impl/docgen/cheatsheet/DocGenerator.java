/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2022 Venice
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jlangch.venice.impl.docgen.cheatsheet;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jlangch.venice.Venice;
import com.github.jlangch.venice.impl.RunMode;
import com.github.jlangch.venice.impl.VeniceInterpreter;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleAnsiSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleAppSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleBenchmarkSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleComponentSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleConfigSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleCryptographySection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleExcelSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleGeoipSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleGradleSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleGrepSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleHexdumpSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleJavaSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleKiraSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleMavenSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleParsifalSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleSemverSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleShellSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleTracingSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.modules.ModuleXmlSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.ArraySection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.ByteBufSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.CidrSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.CollectionsSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.ConcurrencySection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.CsvSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.ExceptionsSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.FunctionsSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.IoFileSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.IoSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.IoZipSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.JavaInteropSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.JsonSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.LazySequencesSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.MacrosSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.MathSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.NamespaceSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.PdfSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.PrimitivesSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.ProtocolsSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.RegexSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.ReplSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.SpecialFormsSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.SystemSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.SystemVarSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.TimeSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.TransducersSection;
import com.github.jlangch.venice.impl.docgen.cheatsheet.section.TypesSection;
import com.github.jlangch.venice.impl.env.Env;
import com.github.jlangch.venice.impl.repl.ReplFunctions;
import com.github.jlangch.venice.impl.util.StringUtil;
import com.github.jlangch.venice.impl.util.io.ClassPathResource;
import com.github.jlangch.venice.impl.util.markdown.Markdown;
import com.github.jlangch.venice.impl.util.markdown.renderer.html.HtmlRenderer;
import com.github.jlangch.venice.javainterop.AcceptAllInterceptor;
import com.lowagie.text.pdf.PdfReader;


public class DocGenerator {

    public DocGenerator(final boolean runExamples) {
        final List<String> preloadedModules = new ArrayList<>();

        preloadedModules
            .addAll(Arrays.asList(
                        "app",    "xml",      "crypt",     "gradle",
                        "trace",  "ansi",     "maven",     "kira",
                        "java",   "semver",   "excel",     "hexdump",
                        "shell",  "geoip",    "benchmark", "component",
                        "config", "parsifal", "grep"));

        final Env docEnv = new VeniceInterpreter(new AcceptAllInterceptor())
                            .createEnv(
                                preloadedModules,
                                false,
                                false,
                                RunMode.DOCGEN)
                            .setStdoutPrintStream(null)
                            .setStderrPrintStream(null);

        // make REPL specific functions available (e.g: 'repl/info')
        final Env env = ReplFunctions.register(docEnv, null, null);

        this.diBuilder = new DocItemBuilder(
                                env,
                                new DocHighlighter(DocColorTheme.getLightTheme()),
                                preloadedModules,
                                runExamples);
    }

    public static List<DocSection> docInfo() {
        return new DocGenerator(false).buildDocInfo();
    }

    public void run(final String version) {
        try {
            System.out.println("Creating cheatsheet V" + version);

            final List<DocSection> left = getLeftSections();
            final List<DocSection> right = getRightSections();
            final List<DocSection> leftModules = getModulesLeftSections();
            final List<DocSection> rightModules = getModulesRightSections();
            final List<MarkdownDoc> topics = getTopics();

            validateUniqueSectionsId(left, right);

            final Map<String,Object> data = new HashMap<>();
            data.put("meta-author", "Venice");
            data.put("version", version);
            data.put("toc", getTOC());
            data.put("left", left);
            data.put("right", right);
            data.put("left-modules", leftModules);
            data.put("right-modules", rightModules);
            data.put("details", getDocItems(concat(left, right, leftModules, rightModules)));
            data.put("snippets", new CodeSnippetReader().readSnippets());
            data.put("topics", topics);

            // [1] create a HTML
            data.put("pdfmode", false);
            final String html = CheatsheetRenderer.renderXHTML(data);
            save(new File(getUserDir(), "cheatsheet.html"), html);

            // [2] create a PDF
            data.put("pdfmode", true);
            final String xhtml = CheatsheetRenderer.renderXHTML(data);
            final ByteBuffer pdf = CheatsheetRenderer.renderPDF(xhtml);
            final byte[] pdfArr =  pdf.array();
            save(new File(getUserDir(), "cheatsheet.pdf"), pdfArr);

            final PdfReader reader = new PdfReader(pdf.array());
            final int pages = reader.getNumberOfPages();
            reader.close();

            System.out.println(String.format(
                    "Generated Cheat Sheet at: %s. XHTML: %dKB, PDF: %dKB / %d pages",
                    getUserDir(),
                    xhtml.length() / 1024,
                    pdfArr.length / 1024,
                    pages));
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private List<DocSection> buildDocInfo() {
        final List<DocSection> sections = new ArrayList<>();
        sections.addAll(getLeftSections());
        sections.addAll(getRightSections());
        return sections;
    }

    private List<DocSection> getTOC() {
        final List<DocSection> content = new ArrayList<>();

        final DocSection primitives = new DocSection("Primitives", "primitives");
        primitives.addSection(new DocSection("Literals", "primitives.literals"));
        primitives.addSection(new DocSection("Numbers", "primitives.numbers"));
        primitives.addSection(new DocSection("Strings", "primitives.strings"));
        primitives.addSection(new DocSection("Chars", "primitives.chars"));
        primitives.addSection(new DocSection("Other", "primitives.other"));
        content.add(primitives);

        final DocSection collections = new DocSection("Collections", "collections");
        collections.addSection(new DocSection("List", "collections.lists"));
        collections.addSection(new DocSection("Vector", "collections.vectors"));
        collections.addSection(new DocSection("Set", "collections.sets"));
        collections.addSection(new DocSection("Map", "collections.maps"));
        collections.addSection(new DocSection("LazySeq", "lazyseq"));
        collections.addSection(new DocSection("Stack", "collections.stack"));
        collections.addSection(new DocSection("Queue", "collections.queue"));
        collections.addSection(new DocSection("DelayQueue", "collections.delayqueue"));
        collections.addSection(new DocSection("DAG", "collections.dag"));
        collections.addSection(new DocSection("Array", "arrays"));
        collections.addSection(new DocSection("ByteBuf", "bytebuf"));
        content.add(collections);

        final DocSection datatypes = new DocSection("Custom\u00A0Types", "datatypes");
        datatypes.addSection(new DocSection("Types", "types"));
        datatypes.addSection(new DocSection("Protocols", "protocols"));
        content.add(datatypes);

        final DocSection functions = new DocSection("Core\u00A0Functions", "functions");
        functions.addSection(new DocSection("Functions", "functions"));
        functions.addSection(new DocSection("Macros", "macros"));
        functions.addSection(new DocSection("Special\u00A0Forms", "specialforms"));
        functions.addSection(new DocSection("Transducers", "transducers"));
        functions.addSection(new DocSection("Namespaces", "namespace"));
        functions.addSection(new DocSection("Exceptions", "exceptions"));
        content.add(functions);

        final DocSection concurrency = new DocSection("Concurrency", "concurrency");
        concurrency.addSection(new DocSection("Atoms", "concurrency.atoms"));
        concurrency.addSection(new DocSection("Futures", "concurrency.futures"));
        concurrency.addSection(new DocSection("Promises", "concurrency.promises"));
        concurrency.addSection(new DocSection("Delay", "concurrency.delay"));
        concurrency.addSection(new DocSection("Agents", "concurrency.agents"));
        concurrency.addSection(new DocSection("Scheduler", "concurrency.scheduler"));
        concurrency.addSection(new DocSection("Locking", "concurrency.locking"));
        concurrency.addSection(new DocSection("Volatiles", "concurrency.volatiles"));
        concurrency.addSection(new DocSection("Parallel", "concurrency.parallel"));
        content.add(concurrency);

        final DocSection threads = new DocSection("Threads", "concurrency.threads");
        threads.addSection(new DocSection("ThreadLocal", "concurrency.threadlocal"));
        threads.addSection(new DocSection("Threads", "concurrency.threads"));
        content.add(threads);

        final DocSection system = new DocSection("System\u00A0&\u00A0Java", "system");
        system.addSection(new DocSection("System", "system"));
        system.addSection(new DocSection("System\u00A0Vars", "sysvars"));
        system.addSection(new DocSection("Java\u00A0Interop", "javainterop"));
        system.addSection(new DocSection("REPL", "repl"));
        content.add(system);

        final DocSection util = new DocSection("Util", "util");
        util.addSection(new DocSection("Math", "math"));
        util.addSection(new DocSection("Time", "time"));
        util.addSection(new DocSection("Regex", "regex"));
        util.addSection(new DocSection("CIDR", "cidr"));

        content.add(util);

        final DocSection io = new DocSection("I/O", "io");
        io.addSection(new DocSection("I/O", "io.util"));
        io.addSection(new DocSection("File", "io.file"));
        io.addSection(new DocSection("Zip/GZip", "io.zip"));
        content.add(io);

        final DocSection documents = new DocSection("Documents", "miscellaneous");
        documents.addSection(new DocSection("JSON", "json"));
        documents.addSection(new DocSection("PDF", "pdf"));
        documents.addSection(new DocSection("PDF Tools", "pdf.pdftools"));
        documents.addSection(new DocSection("CSV", "csv"));
        documents.addSection(new DocSection("XML", "modules.xml"));
        documents.addSection(new DocSection("Excel", "modules.excel"));
        content.add(documents);

        final DocSection extmod = new DocSection("Modules", "modules");
        extmod.addSection(new DocSection("Kira\u00A0Templates", "modules.kira"));
        extmod.addSection(new DocSection("Parsifal", "modules.parsifal"));
        extmod.addSection(new DocSection("Configuration", "modules.config"));
        extmod.addSection(new DocSection("Component", "modules.component"));
        extmod.addSection(new DocSection("XML", "modules.xml"));
        extmod.addSection(new DocSection("Grep", "modules.grep"));
        extmod.addSection(new DocSection("Cryptography", "modules.cryptography"));
        extmod.addSection(new DocSection("Java", "modules.java"));
        extmod.addSection(new DocSection("Semver", "modules.semver"));
        extmod.addSection(new DocSection("Hexdump", "modules.hexdump"));
        extmod.addSection(new DocSection("Shell", "modules.shell"));
        extmod.addSection(new DocSection("Geo IP", "modules.geoip"));
        extmod.addSection(new DocSection("Ansi", "modules.ansi"));
        extmod.addSection(new DocSection("Gradle", "modules.gradle"));
        extmod.addSection(new DocSection("Maven", "modules.maven"));
        extmod.addSection(new DocSection("Tracing", "modules.tracing"));
        extmod.addSection(new DocSection("Benchmark", "modules.benchmark"));
        extmod.addSection(new DocSection("App", "modules.app"));
        content.add(extmod);

        final DocSection others = new DocSection("Others", "others");
        others.addSection(new DocSection("Embedding in Java", "embedding"));
        others.addSection(new DocSection("Venice Doc", "venicedoc"));
        others.addSection(new DocSection("Markdown", "markdown"));
        content.add(others);

        return content;
    }

    private List<MarkdownDoc> getTopics() {
        final List<MarkdownDoc> topics = new ArrayList<>();

        topics.add(new MarkdownDoc(
                        "VeniceDoc",
                        new HtmlRenderer().render(loadVeniceDocMarkdown()),
                        "venicedoc"));

        topics.add(new MarkdownDoc(
                        "Markdown",
                        new HtmlRenderer().render(loadMarkdownDoc()),
                        "markdown"));

        return topics;
    }

    private List<DocSection> getLeftSections() {
        return Arrays.asList(
                new PrimitivesSection(diBuilder).section(),
                new ByteBufSection(diBuilder).section(),
                new RegexSection(diBuilder).section(),
                new MathSection(diBuilder).section(),
                new TransducersSection(diBuilder).section(),
                new FunctionsSection(diBuilder).section(),
                new MacrosSection(diBuilder).section(),
                new SpecialFormsSection(diBuilder).section(),
                new ExceptionsSection(diBuilder).section(),
                new TypesSection(diBuilder).section(),
                new ProtocolsSection(diBuilder).section(),
                new NamespaceSection(diBuilder).section(),
                new JavaInteropSection(diBuilder).section(),
                new ReplSection(diBuilder).section(),
                new PdfSection(diBuilder).section(),
                new IoZipSection(diBuilder).section());
    }

    private List<DocSection> getRightSections() {
        return Arrays.asList(
                new CollectionsSection(diBuilder).section(),
                new LazySequencesSection(diBuilder).section(),
                new ArraySection(diBuilder).section(),
                new ConcurrencySection(diBuilder).section(),
                new SystemSection(diBuilder).section(),
                new SystemVarSection(diBuilder).section(),
                new TimeSection(diBuilder).section(),
                new IoSection(diBuilder).section(),
                new IoFileSection(diBuilder).section(),
                new JsonSection(diBuilder).section(),
                new CidrSection(diBuilder).section(),
                new CsvSection(diBuilder).section());
    }

    private List<DocSection> getModulesLeftSections() {
        return Arrays.asList(
                new ModuleKiraSection(diBuilder).section(),
                new ModuleCryptographySection(diBuilder).section(),
                new ModuleXmlSection(diBuilder).section(),
                new ModuleJavaSection(diBuilder).section(),
                new ModuleParsifalSection(diBuilder).section(),
                new ModuleGradleSection(diBuilder).section(),
                new ModuleMavenSection(diBuilder).section(),
                new ModuleTracingSection(diBuilder).section(),
                new ModuleShellSection(diBuilder).section(),
                new ModuleAnsiSection(diBuilder).section(),
                new ModuleGrepSection(diBuilder).section());
    }

    private List<DocSection> getModulesRightSections() {
        return Arrays.asList(
                new ModuleHexdumpSection(diBuilder).section(),
                new ModuleSemverSection(diBuilder).section(),
                new ModuleGeoipSection(diBuilder).section(),
                new ModuleExcelSection(diBuilder).section(),
                new ModuleConfigSection(diBuilder).section(),
                new ModuleComponentSection(diBuilder).section(),
                new ModuleAppSection(diBuilder).section(),
                new ModuleBenchmarkSection(diBuilder).section());
    }

    private List<DocItem> getDocItems(final List<DocSection> sections) {
        return sections
                .stream()
                .map(s -> s.getSections())
                .flatMap(List::stream)
                .map(s -> s.getSections())
                .flatMap(List::stream)
                .map(s -> s.getItems())
                .flatMap(List::stream)
                .filter(i -> !StringUtil.isBlank(i.getName()))
                .distinct()
                .sorted(Comparator.comparing(DocItem::getName))
                .collect(Collectors.toList());
    }

    private List<DocSection> concat(
            final List<DocSection> s1,
            final List<DocSection> s2,
            final List<DocSection> s3,
            final List<DocSection> s4
    ) {
        final List<DocSection> list = new ArrayList<>();
        list.addAll(s1);
        list.addAll(s2);
        list.addAll(s3);
        list.addAll(s4);
        return list;
    }

    private void save(final File file, final String text) throws Exception {
        save(file, text.getBytes("UTF-8"));
    }

    private void save(final File file, final byte[] data) throws Exception {
        try(FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data, 0, data.length);
            fos.flush();
        }
    }

    private File getUserDir() {
        return new File(System.getProperty("user.dir"));
    }

    private Markdown loadVeniceDocMarkdown() {
        try {
            return Markdown.parse(
                        new ClassPathResource(Venice.class.getPackage(), "docgen/venice-doc.md")
                            .getResourceAsString("UTF-8"));
        }
        catch(RuntimeException ex) {
            throw new RuntimeException("Failed to read 'venice-doc.md'!", ex);
        }
    }

    private Markdown loadMarkdownDoc() {
        try {
            return Markdown.parse(
                        new ClassPathResource(Venice.class.getPackage(), "docgen/markdown-doc.md")
                            .getResourceAsString("UTF-8"));
        }
        catch(RuntimeException ex) {
            throw new RuntimeException("Failed to read 'markdown-doc.md'!", ex);
        }
    }

    private final void validateUniqueSectionsId(
            final List<DocSection> left,
            final List<DocSection> right
    ) {
        final Set<String> ids = new HashSet<>();

        left.forEach(s -> validateUniqueSectionId(s, ids));
        right.forEach(s -> validateUniqueSectionId(s, ids));
    }

    private final void validateUniqueSectionId(
            final DocSection section,
            final Set<String> ids
    ) {
        final String id = section.getId();
        if (id != null) {
            if (ids.contains(section.getId())) {
                throw new RuntimeException(
                        String.format(
                                "Non unique section id %s on section %s",
                                id, section.getTitle()));
            }
            ids.add(id);
        }

        // recursively validate children
        section.getSections().forEach(s -> validateUniqueSectionId(s, ids));
    }


    private final DocItemBuilder diBuilder;
}
