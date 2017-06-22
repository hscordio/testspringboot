package hello;

public class ApplePayMerchantValidationRequestData
{
	String merchantIdentifier;
	String domainName;
	String displayName;

	public String getMerchantIdentifier()
	{
		return merchantIdentifier;
	}

	public void setMerchantIdentifier(final String merchantIdentifier)
	{
		this.merchantIdentifier = merchantIdentifier;
	}

	public String getDomainName()
	{
		return domainName;
	}

	public void setDomainName(final String domainName)
	{
		this.domainName = domainName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(final String displayName)
	{
		this.displayName = displayName;
	}
}
