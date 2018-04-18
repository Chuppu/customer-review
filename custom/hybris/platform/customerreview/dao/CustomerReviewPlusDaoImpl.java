package custom.hybris.platform.customerreview.dao;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.dao.CustomerReviewDao;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import java.util.Collections;
import java.util.List;

public class CustomerReviewPlusDaoImpl extends DefaultCustomerReviewDao implements CustomerReviewPlusDao {
	
	
	public int getTotalReviewsWithinRange(ProductModel paramProductModel, Double min, Double max) {
			 String query = "SELECT {" + Item.PK + "} FROM {" + "CustomerReview" + "} WHERE {" + "product" + "}=?product AND rating >=?min AND rating <=?max";
			 FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query);
			 fsQuery.addQueryParameter("product", product);
			 fsQuery.addQueryParameter("min", min);
			 fsQuery.addQueryParameter("max", max);
			 fsQuery.setResultClassList(Collections.singletonList(CustomerReviewModel.class));

			 SearchResult<CustomerReviewModel> searchResult = getFlexibleSearchService().search(fsQuery);
			 return CollectionUtils.isNotEmpty(searchResult.getResult()) ? searchResult.getResult().length() : 0;
	}
	
	public List<String> getCurseWords(LanguageModel language) {
		String query = "SELECT  {" + Item.PK + "} FROM {" + "BlackListTerms" + "} WHERE {" + "language" + "}=?language";
		FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(query);
		fsQuery.addQueryParameter("language", language);
		fsQuery.setResultClassList(Collections.singletonList(String.class));
		SearchResult<String> searchResult = getFlexibleSearchService().search(fsQuery);
		return searchResult.getResult();
	}
}