package custom.hybris.platform.customerreview.dao;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import java.util.List;

public interface CustomerReviewPlusDao extends CustomerReviewDao 
{
  public abstract int getTotalReviewsWithinRange(ProductModel paramProductModel, Double min, Double max);
  public abstract List<String> getCurseWords(LanguageModel language);
}