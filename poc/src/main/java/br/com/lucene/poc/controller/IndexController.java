package br.com.lucene.poc.controller;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
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
	public void index(){
		
	}
	
	@Post
	@Path("/indexar")
	public void indexar(String texto){
		log.info("Executando /indexar com o texto: "+ texto);
		try {
			// 0. Specify the analyzer for tokenizing text.
			// The same analyzer should be used for indexing and searching
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
	
			// 1. create the index
			// Directory index = new RAMDirectory();
			File indexDir = new File("./index");
	
			Directory index = FSDirectory.open(indexDir);
	
			// the boolean arg in the IndexWriter ctor means to
			// create a new index, overwriting any existing index
			IndexWriter w = new IndexWriter(index, analyzer, true, IndexWriter.MaxFieldLength.UNLIMITED);
			addDoc(w, texto);
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Post
	@Path("/procurar")
	public void procurar(String parametro){
		log.info("Executando /procurar com o parametro: "+ parametro);
		try{
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
			// 2. query
			String querystr = parametro;
			
			// 1. create the index
			// Directory index = new RAMDirectory();
			File indexDir = new File("./index");
	
			Directory index = FSDirectory.open(indexDir);
		
			// the "title" arg specifies the default field to use
			// when no field is explicitly specified in the query.
			Query q = new QueryParser(Version.LUCENE_CURRENT, "title", analyzer).parse(querystr);
		
			// 3. search
			int hitsPerPage = 10;
			IndexSearcher searcher = new IndexSearcher(index, true);
			TopScoreDocCollector collector = TopScoreDocCollector.create(
					hitsPerPage, true);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
		
			// 4. display results
			log.info("Found " + hits.length + " hits.");
			for (int i = 0; i < hits.length; ++i) {
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				log.info((i + 1) + ". " + d.get("title"));
			}
		
			// searcher can only be closed when there
			// is no need to access the documents any more.
			searcher.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void addDoc(IndexWriter w, String value) throws IOException {
		Document doc = new Document();
		doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
		w.updateDocument(new Term("title"), doc);
	}
}
