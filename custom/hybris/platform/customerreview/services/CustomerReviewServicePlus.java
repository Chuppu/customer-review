package custom.hybris.platform.customerreview.services;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.dao.CustomerReviewDao;
import de.hybris.platform.customerreview.jalo.CustomerReview;
import de.hybris.platform.customerreview.jalo.CustomerReviewManager;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Required;

public class CustomerReviewServicePlus extends DefaultCustomerReviewService {
	private CustomerReviewPlusDao customerReviewPlusDao;
	//Create a default language model.
	public static final LanguageModel DEFAULT_LANGUAGE_MODEL = LanguageModel.builder().language("en").locale("US").build;
	private final LoadingCache<LanguageModel, List<String>> curseWordCache;

	public Integer getTotalReviewsWithinRange(ProductModel product, Double min, Double max) {
		ServicesUtil.validateParameterNotNullStandardMessage("product", product);
		return getCustomerReviewPlusDao().getTotalReviewsWithinRange(product, min, max);
	}
	
	@Override
	public CustomerReviewModel createCustomerReview(Double rating, String headline, String comment, UserModel user, ProductModel product) {
		//Simple tokenizer
		Set<String> tokens = new HashSet(Arrays.asList(StringUtils.split(StringUtils.toLowercase(headline) + " " + StringUtils.toLowercase(comment), ",. ;:!")));
		Set<String> curseWords = new HashSet(listCurseWords());
		
		//if headline/comment contains any curse words throw exception
		if (CollectionUtils.isNotEmpty(tokens.retainAll(curseWords))) {
			log.info(String.format("Curse words found in Headline: %s Comment: %s", headline, comment);
			throw new InvalidDataException("Comment or Headline contains curse words.");
		}
		
		if (rating < 0) {
			throw new InvalidArgumentException("Rating cannot be negative.");
		}

		// Call super to create and return the CustomerReviewModel
		super.createCustomerReview(rating, headline, comment, user, product)
	}

	public List<String> listCurseWords() {
		buildCurseWordCache()
		return curseWordCache.getUnchecked(DEFAULT_LANGUAGE_MODEL);
	}

	public List<String> listCurseWords(LanguageModel language) {
		buildCurseWordCache()
		return curseWordCache.getUnchecked(language);
	}
	
	private void buildCurseWordCache() {
		if (this.curseWordCache == null) { // Ensures data not re-cached
			curseWordCache = CacheBuilder.newBuilder()
                	//update the cache once in 3 hours
	                .expireAfterWrite(3, TimeUnit.HOURS)
        	        .build(CacheLoader.from(this::listCurseWordsCached));
		}
	}
	
	private List<String> listCurseWordsCached(LanguageModel language) {
		return getCustomerReviewPlusDao().getCurseWords(language);
	}
	
	protected CustomerReviewPlusDao getCustomerReviewPlusDao() {
		return this.customerReviewPlusDao;
	}
	
	@Required
	public void setCustomerReviewPlusDao (CustomerReviewPlusDao customerReviewPlusDao) {
		super.setCustomerReviewDao((CustomerReviewDao)customerReviewPlusDao);
		this.customerReviewPlusDao = customerReviewPlusDao;
	}
}