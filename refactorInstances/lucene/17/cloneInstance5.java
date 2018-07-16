        while(true) {
          final BytesRef term = termsEnum.next();
          if (term == null) {
            break;
          }
          final double termval = parser.parseDouble(term);
          if (retArray == null) {
            // late init so numeric fields don't double allocate
            retArray = new double[maxDoc];
          }

          docs = termsEnum.docs(null, docs, DocsEnum.FLAG_NONE);
          while (true) {
            final int docID = docs.nextDoc();
            if (docID == DocIdSetIterator.NO_MORE_DOCS) {
              break;
            }
            retArray[docID] = termval;
            if (setDocsWithField) {
              if (docsWithField == null) {
                // Lazy init
                docsWithField = new FixedBitSet(maxDoc);
              }
              docsWithField.set(docID);
            }
          }
        }
