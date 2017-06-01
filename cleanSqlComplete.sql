delete from DICTIONARY_TERMS_RELATIONS where DICTIONARY_TERMS_RELATIONS.term1_id in (select term_id FROM dictionary_terms where term_text in ('a', 'a a','about', 'above', 'across', 'after', 'afterwards', 'again', 'against', 'all', 'almost', 'alone', 'along', 'already', 'also','although','always','am','among', 'amongst', 'amoungst', 'amount',  'an', 'and', 'another', 'any','anyhow','anyone','anything','anyway', 'anywhere', 'are', 'around', 'as',  'at',
								   'back','be','became', 'because','become','becomes', 'becoming', 'been', 'before', 'beforehand', 'behind', 'being', 'below', 'beside', 'besides', 'between', 'beyond', 'bill', 'both', 'bottom','but', 'by',
								   'call', 'can', 'cannot', 'cant', 'co', 'con', 'could', 'couldnt', 'cry',
								   'de', 'describe', 'do', 'done', 'down', 'due', 'during',
								   'each', 'eg', 'eight', 'either', 'else', 'elsewhere', 'enough', 'etc', 'even', 'ever', 'every', 'everyone', 'everything', 'everywhere',
								   'for', 'from', 'get',
								   'give', 'go',
								   'had', 'has', 'hasnt', 'have', 'he', 'hence', 'her', 'here', 'hereafter', 'hereby', 'herein', 'hereupon', 'hers', 'herself', 'him', 'himself', 'his', 'how', 'however',
								   'i', 'i.e.', 'if', 'in', 'inc', 'indeed', 'into', 'is', 'it', 'its', 'itself',
								   'latter', 'latterly', 'least', 'ltd',
								   'made', 'many', 'may', 'me', 'meanwhile', 'might', 'mill', 'mine', 'more', 'moreover', 'most', 'mostly', 'much', 'must', 'my', 'myself',
								   'namely', 'neither', 'never', 'nevertheless', 'no', 'nobody', 'none', 'noone', 'nor', 'not', 'nothing', 'now', 'nowhere',
								   'of', 'off', 'often', 'on', 'once', 'one', 'only', 'onto', 'or', 'other', 'others', 'otherwise', 'our', 'ours', 'ourselves', 'out', 'over',
								   'per', 'perhaps', 'please', 'put',
								   'rather', 're',
								   'same', 'see', 'seem', 'seemed', 'seeming', 'seems', 'serious', 'several', 'she', 'should', 'show', 'side', 'since', 'sincere', 'six', 'sixty', 'so', 'some', 'somehow', 'someone', 'something', 'sometime', 'sometimes', 'somewhere', 'still', 'such',
								   'than', 'that', 'the', 'their', 'them', 'themselves', 'then', 'thence', 'there', 'thereafter', 'thereby', 'therefore', 'therein', 'thereupon', 'these', 'they', 'thickv', 'thin', 'this', 'those', 'though', 'through', 'throughout', 'thru', 'thus', 'to', 'together', 'too', 'top', 'toward', 'towards',
								   'un', 'under', 'until', 'up', 'upon', 'us',
								   'very', 'via',
								   'was', 'we', 'well', 'were', 'what', 'whatever', 'when', 'whence', 'whenever', 'where', 'whereafter', 'whereas', 'whereby', 'wherein', 'whereupon', 'wherever', 'whether', 'which', 'while', 'whither', 'who', 'whoever', 'whole', 'whom', 'whose', 'why', 'will', 'with', 'within', 'without', 'would',
								   'yet', 'you', 'your', 'yours', 'yourself', 'yourselves'));
delete from DICTIONARY_TERMS_RELATIONS where DICTIONARY_TERMS_RELATIONS.term1_id in (select term_id from dictionary_terms where abs(term_text) > 0 and not term_text like '%.%');
delete from DICTIONARY_TERMS_RELATIONS where DICTIONARY_TERMS_RELATIONS.term1_id in (select term_id from dictionary_terms where term_text like '%>%');							   
delete from DICTIONARY_TERMS_RELATIONS where DICTIONARY_TERMS_RELATIONS.term1_id in (select term_id from dictionary_terms where term_text like '%subject%' or term_text like '%dear%' or term_text like '%r&d%' or term_text like '%floor%' or term_text like '% sp %' or term_text like '%support%');
delete from DICTIONARY_TERMS_RELATIONS where DICTIONARY_TERMS_RELATIONS.term1_id in (SELECT term_id FROM dictionary_terms where (term_text like '%sunday%' or  term_text like '%monday%' or term_text like 'mon %' or term_text like '%tuesday%' or term_text like 'tue %' or term_text like '%wednesday%' or term_text like 'wed %' or term_text like '%thursday%' or term_text like 'thu %' or term_text like '%friday%' or term_text like 'fri %' or term_text like '%saturday%' or term_text like 'sat %'));
delete from DICTIONARY_TERMS_RELATIONS where DICTIONARY_TERMS_RELATIONS.term1_id in (SELECT term_id FROM dictionary_terms where (term_text like '%january%' or  term_text like 'jan %' or  term_text like '%february%' or  term_text like 'feb %' or term_text like '%march%' or term_text like 'mar %' or term_text like '%april%' or term_text like 'apr %' or term_text like '%may%' or term_text like '%june%' or term_text like 'jun %' or term_text like '%july%' or term_text like 'jul %' or term_text like '%august%' or term_text like 'aug %' or term_text like '%september%' or term_text like 'sep %' or term_text like '%october%' or term_text like 'oct %' or term_text like '%november%' or term_text like 'nov %' or term_text like '%december%' or term_text like 'dec %'));
delete from DICTIONARY_TERMS_RELATIONS where DICTIONARY_TERMS_RELATIONS.term1_id in (select term_id from dictionary_terms where Length(term_text) < 2);

delete from DICTIONARY_TERMS_DOMAINS where DICTIONARY_TERMS_DOMAINS.term_id in (select term_id FROM dictionary_terms where term_text in ('a', 'a a', 'about', 'above', 'across', 'after', 'afterwards', 'again', 'against', 'all', 'almost', 'alone', 'along', 'already', 'also','although','always','am','among', 'amongst', 'amoungst', 'amount',  'an', 'and', 'another', 'any','anyhow','anyone','anything','anyway', 'anywhere', 'are', 'around', 'as',  'at',
								   'back','be','became', 'because','become','becomes', 'becoming', 'been', 'before', 'beforehand', 'behind', 'being', 'below', 'beside', 'besides', 'between', 'beyond', 'bill', 'both', 'bottom','but', 'by',
								   'call', 'can', 'cannot', 'cant', 'co', 'con', 'could', 'couldnt', 'cry',
								   'de', 'describe', 'do', 'done', 'down', 'due', 'during',
								   'each', 'eg', 'eight', 'either', 'else', 'elsewhere', 'enough', 'etc', 'even', 'ever', 'every', 'everyone', 'everything', 'everywhere',
								   'for', 'from', 'get',
								   'give', 'go',
								   'had', 'has', 'hasnt', 'have', 'he', 'hence', 'her', 'here', 'hereafter', 'hereby', 'herein', 'hereupon', 'hers', 'herself', 'him', 'himself', 'his', 'how', 'however',
								   'i', 'i.e.', 'if', 'in', 'inc', 'indeed', 'into', 'is', 'it', 'its', 'itself',
								   'latter', 'latterly', 'least', 'ltd',
								   'made', 'many', 'may', 'me', 'meanwhile', 'might', 'mill', 'mine', 'more', 'moreover', 'most', 'mostly', 'much', 'must', 'my', 'myself',
								   'namely', 'neither', 'never', 'nevertheless', 'no', 'nobody', 'none', 'noone', 'nor', 'not', 'nothing', 'now', 'nowhere',
								   'of', 'off', 'often', 'on', 'once', 'one', 'only', 'onto', 'or', 'other', 'others', 'otherwise', 'our', 'ours', 'ourselves', 'out', 'over',
								   'per', 'perhaps', 'please', 'put',
								   'rather', 're',
								   'same', 'see', 'seem', 'seemed', 'seeming', 'seems', 'serious', 'several', 'she', 'should', 'show', 'side', 'since', 'sincere', 'six', 'sixty', 'so', 'some', 'somehow', 'someone', 'something', 'sometime', 'sometimes', 'somewhere', 'still', 'such',
								   'than', 'that', 'the', 'their', 'them', 'themselves', 'then', 'thence', 'there', 'thereafter', 'thereby', 'therefore', 'therein', 'thereupon', 'these', 'they', 'thickv', 'thin', 'this', 'those', 'though', 'through', 'throughout', 'thru', 'thus', 'to', 'together', 'too', 'top', 'toward', 'towards',
								   'un', 'under', 'until', 'up', 'upon', 'us',
								   'very', 'via',
								   'was', 'we', 'well', 'were', 'what', 'whatever', 'when', 'whence', 'whenever', 'where', 'whereafter', 'whereas', 'whereby', 'wherein', 'whereupon', 'wherever', 'whether', 'which', 'while', 'whither', 'who', 'whoever', 'whole', 'whom', 'whose', 'why', 'will', 'with', 'within', 'without', 'would',
								   'yet', 'you', 'your', 'yours', 'yourself', 'yourselves'));
delete from DICTIONARY_TERMS_DOMAINS where DICTIONARY_TERMS_DOMAINS.term_id in (select term_id from dictionary_terms where abs(term_text) > 0 and not term_text like '%.%');
delete from DICTIONARY_TERMS_DOMAINS where DICTIONARY_TERMS_DOMAINS.term_id in (select term_id from dictionary_terms where term_text like '%>%');
delete from DICTIONARY_TERMS_DOMAINS where DICTIONARY_TERMS_DOMAINS.term_id in (select term_id from dictionary_terms where term_text like '%subject%' or term_text like '%dear%' or term_text like '%r&d%' or term_text like '%floor%' or term_text like '% sp %' or term_text like '%support%' );
delete from DICTIONARY_TERMS_DOMAINS where DICTIONARY_TERMS_DOMAINS.term_id in (SELECT term_id FROM dictionary_terms where (term_text like '%sunday%' or  term_text like '%monday%' or term_text like 'mon %' or term_text like '%tuesday%' or term_text like 'tue %' or term_text like '%wednesday%' or term_text like 'wed %' or term_text like '%thursday%' or term_text like 'thu %' or term_text like '%friday%' or term_text like 'fri %' or term_text like '%saturday%' or term_text like 'sat %'));
delete from DICTIONARY_TERMS_DOMAINS where DICTIONARY_TERMS_DOMAINS.term_id in (SELECT term_id FROM dictionary_terms where (term_text like '%january%' or  term_text like 'jan %' or  term_text like '%february%' or  term_text like 'feb %' or term_text like '%march%' or term_text like 'mar %' or term_text like '%april%' or term_text like 'apr %' or term_text like '%may%' or term_text like '%june%' or term_text like 'jun %' or term_text like '%july%' or term_text like 'jul %' or term_text like '%august%' or term_text like 'aug %' or term_text like '%september%' or term_text like 'sep %' or term_text like '%october%' or term_text like 'oct %' or term_text like '%november%' or term_text like 'nov %' or term_text like '%december%' or term_text like 'dec %'));
delete from DICTIONARY_TERMS_DOMAINS where DICTIONARY_TERMS_DOMAINS.term_id in (select term_id from dictionary_terms where Length(term_text) < 2);

delete from DICTIONARY_TERMS where DICTIONARY_TERMS.term_id in (select term_id FROM dictionary_terms where term_text in ('a', 'a a', 'about', 'above', 'across', 'after', 'afterwards', 'again', 'against', 'all', 'almost', 'alone', 'along', 'already', 'also','although','always','am','among', 'amongst', 'amoungst', 'amount',  'an', 'and', 'another', 'any','anyhow','anyone','anything','anyway', 'anywhere', 'are', 'around', 'as',  'at',
								   'back','be','became', 'because','become','becomes', 'becoming', 'been', 'before', 'beforehand', 'behind', 'being', 'below', 'beside', 'besides', 'between', 'beyond', 'bill', 'both', 'bottom','but', 'by',
								   'call', 'can', 'cannot', 'cant', 'co', 'con', 'could', 'couldnt', 'cry',
								   'de', 'describe', 'do', 'done', 'down', 'due', 'during',
								   'each', 'eg', 'eight', 'either', 'else', 'elsewhere', 'enough', 'etc', 'even', 'ever', 'every', 'everyone', 'everything', 'everywhere',
								   'for', 'from', 'get',
								   'give', 'go',
								   'had', 'has', 'hasnt', 'have', 'he', 'hence', 'her', 'here', 'hereafter', 'hereby', 'herein', 'hereupon', 'hers', 'herself', 'him', 'himself', 'his', 'how', 'however',
								   'i', 'i.e.', 'if', 'in', 'inc', 'indeed', 'into', 'is', 'it', 'its', 'itself',
								   'latter', 'latterly', 'least', 'ltd',
								   'made', 'many', 'may', 'me', 'meanwhile', 'might', 'mill', 'mine', 'more', 'moreover', 'most', 'mostly', 'much', 'must', 'my', 'myself',
								   'namely', 'neither', 'never', 'nevertheless', 'no', 'nobody', 'none', 'noone', 'nor', 'not', 'nothing', 'now', 'nowhere',
								   'of', 'off', 'often', 'on', 'once', 'one', 'only', 'onto', 'or', 'other', 'others', 'otherwise', 'our', 'ours', 'ourselves', 'out', 'over',
								   'per', 'perhaps', 'please', 'put',
								   'rather', 're',
								   'same', 'see', 'seem', 'seemed', 'seeming', 'seems', 'serious', 'several', 'she', 'should', 'show', 'side', 'since', 'sincere', 'six', 'sixty', 'so', 'some', 'somehow', 'someone', 'something', 'sometime', 'sometimes', 'somewhere', 'still', 'such',
								   'than', 'that', 'the', 'their', 'them', 'themselves', 'then', 'thence', 'there', 'thereafter', 'thereby', 'therefore', 'therein', 'thereupon', 'these', 'they', 'thickv', 'thin', 'this', 'those', 'though', 'through', 'throughout', 'thru', 'thus', 'to', 'together', 'too', 'top', 'toward', 'towards',
								   'un', 'under', 'until', 'up', 'upon', 'us',
								   'very', 'via',
								   'was', 'we', 'well', 'were', 'what', 'whatever', 'when', 'whence', 'whenever', 'where', 'whereafter', 'whereas', 'whereby', 'wherein', 'whereupon', 'wherever', 'whether', 'which', 'while', 'whither', 'who', 'whoever', 'whole', 'whom', 'whose', 'why', 'will', 'with', 'within', 'without', 'would',
								   'yet', 'you', 'your', 'yours', 'yourself', 'yourselves'));
delete from DICTIONARY_TERMS where DICTIONARY_TERMS.term_id in (select term_id from dictionary_terms where abs(term_text) > 0 and not term_text like '%.%');
delete from DICTIONARY_TERMS where DICTIONARY_TERMS.term_id in (select term_id from dictionary_terms where term_text like '%>%');
delete from DICTIONARY_TERMS where DICTIONARY_TERMS.term_id in (select term_id from dictionary_terms where term_text like '%subject%' or term_text like '%dear%' or term_text like '%r&d%' or term_text like '%floor%' or term_text like '% sp %' or term_text like '%support%');
delete from DICTIONARY_TERMS where DICTIONARY_TERMS.term_id in (SELECT term_id FROM dictionary_terms where (term_text like '%sunday%' or  term_text like '%monday%' or term_text like 'mon %' or term_text like '%tuesday%' or term_text like 'tue %' or term_text like '%wednesday%' or term_text like 'wed %' or term_text like '%thursday%' or term_text like 'thu %' or term_text like '%friday%' or term_text like 'fri %' or term_text like '%saturday%' or term_text like 'sat %'));
delete from DICTIONARY_TERMS where DICTIONARY_TERMS.term_id in (SELECT term_id FROM dictionary_terms where (term_text like '%january%' or  term_text like 'jan %' or  term_text like '%february%' or  term_text like 'feb %' or term_text like '%march%' or term_text like 'mar %' or term_text like '%april%' or term_text like 'apr %' or term_text like '%may%' or term_text like '%june%' or term_text like 'jun %' or term_text like '%july%' or term_text like 'jul %' or term_text like '%august%' or term_text like 'aug %' or term_text like '%september%' or term_text like 'sep %' or term_text like '%october%' or term_text like 'oct %' or term_text like '%november%' or term_text like 'nov %' or term_text like '%december%' or term_text like 'dec %'));

delete from DICTIONARY_TERMS where DICTIONARY_TERMS.term_id in (select term_id from dictionary_terms where Length(term_text) < 2);

update dictionary_terms set term_text=replace(term_text,'(','') where term_text like '%(%';
update dictionary_terms set term_text=replace(term_text,')','') where term_text like '%)%';
update dictionary_terms set term_stemmed_text=replace(term_text,'(','') where term_stemmed_text like '%(%';
update dictionary_terms set term_stemmed_text=replace(term_text,')','') where term_stemmed_text like '%)%';

delete from dictionary_terms_relations where term2_id not in (select term_id from dictionary_terms);
