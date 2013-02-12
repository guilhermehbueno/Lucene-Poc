package br.com.lucene.poc.controller;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;

@Resource
public class IndexController {

	private static final Logger log = Logger.getLogger(IndexController.class);

	@Path("/")
	public void index() {

	}

	@Post
	@Path("/indexar")
	public void indexar(String texto) {
		log.info("Executando /indexar com o texto: " + texto);
		try {
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_36, analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			//iwc.setOpenMode(OpenMode.CREATE);
			File indexDir = new File("./index");
			Directory index = FSDirectory.open(indexDir);
			IndexWriter writer = new IndexWriter(index, iwc);
			addDoc(writer, texto);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Post
	@Path("/procurar")
	public void procurar(String parametro) {
		log.info("Executando /procurar com o parametro: " + parametro);
		try {
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			String querystr = parametro;
			Query q = new QueryParser(Version.LUCENE_36, "title", analyzer).parse(querystr);
			
			int hitsPerPage = 10;
			IndexReader reader = IndexReader.open(FSDirectory.open(new File("./index")));
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			log.info("Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				log.info((i + 1) + ". " + d.get("title"));
			}

			searcher.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addDoc(IndexWriter w, String value) throws IOException {
		Document doc = new Document();
		doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
		w.updateDocument(new Term("title"), doc);
	}
}
