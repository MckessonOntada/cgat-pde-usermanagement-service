package com.mckesson.cgat.pde.usermanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

/**
 *
 *
 * import org.springframework.data.jpa.repository.Meta;
 *
 * import jakarta.persistence.Column; import jakarta.persistence.Entity; import
 * jakarta.persistence.GeneratedValue; import jakarta.persistence.Id; import
 * jakarta.persistence.SequenceGenerator; import jakarta.persistence.Table; import
 * jakarta.validation.constraints.Digits; import jakarta.validation.constraints.NotBlank;
 * import jakarta.validation.constraints.NotNull; import
 * jakarta.validation.constraints.Size;Copy below prompt to generate class github copilot
 *
 * Generate java class with following csv
 *
 * Description,Data type,Length,Decimal Places,Mandatory (M) Or Optional (O),What this
 * field represents Sold To Customer,CHAR,10,0,M,The customer who orders the goods or
 * services Sold To Customer Name,CHAR,35,0,M,Name of the Ordering Organization Ship To
 * Customer,CHAR,10,0,M,The party who receives delivery of the goods Ship To Customer
 * Name,CHAR,35,0,M,Name of the goods receiving organization Parent Customer
 * ,CHAR,10,0,O,Customer number of the higher-level customer hierarchy Parent Customer
 * Name,CHAR,35,0,O,Name of Customer of the higher-level customer hierarchy Sales Org
 * (MPB/Specialty/3PL),CHAR,4,0,M,"organizational unit responsible for the sale products
 * or services,legal liability for products and customer claims." Dist
 * Channel,CHAR,2,0,M,The way in which products or services reach the customer
 * Division,CHAR,2,0,M,"A way of grouping materials, products, or services. " SAP
 * Material,CHAR,18,0,M,Alphanumeric key uniquely identifying the material.
 * Variant,CHAR,30,0,O,With in the Material different types of variations Variant
 * Description,CHAR,30,0,O,Name of the Variant Material Identifier for
 * CGAT,CHAR,1,0,M,CGAT material Identifier NDC,CHAR,18,0,M,"Drug products are identified
 * and reported using a unique, three-segment number, called the National Drug Code (NDC),
 * which is a universal product identifier for human drugs" UDI,CHAR,18,0,O,Unique Device
 * Identification (UDI) only using for medical device identifier similar to NDC. Material
 * Description in language EN,CHAR,40,0,M,Text that describes the material in more detail
 * Sell Unit,CHAR,3,0,M,Unit of measure in which the material is sold Contract
 * No,CHAR,10,0,O,It’s a contractual agreement between two parties for a product & price
 * with a particular validity. Contract Type,CHAR,4,0,O,Contract types used in case same
 * customer exist in multiple contracts which one take precedence. Contract Type
 * Description,CHAR,40,0,O,Name of the contract type Contract Valid
 * From,CHAR,10,0,O,Contract Start date Contract Valid To,CHAR,10,0,O,Contract End date
 * Lead Number(MPB),CHAR,10,0,O,MPB related Lead number using in Pricing Buying
 * Group,CHAR,10,0,O,Similar buying metrics Customers grouping together into one group
 * Buying Group Description,CHAR,40,0,O,Name of Buying Group Customer identifier
 * ,CHAR,3,0,O,Indicates the category of a Customer Pricing Date,CHAR,10,0,M,"The date
 * that determines date-related pricing elements, such as conditions and foreign exchange
 * rate." Net Price,CHAR,18,2,M,The net price that results from the net value divided by
 * the order quantity Regular/List Price,CHAR,18,2,O,The price at which the goods or
 * services are regularly sold with NO promotions/discounts. Contract
 * Price,CHAR,18,2,O,Contract price agreed both parties for a particular time period. WAC
 * Price,CHAR,18,2,M,"WAC stands for Wholesale Acquisition Cost, which is the price a
 * manufacturer lists for a drug to direct purchasers. It's the price before any
 * discounts, rebates, allowances, or other price" Discounts,CHAR,18,2,O,Discounts are
 * price-reduced transactions that you configure on an as-needed basis. They can be
 * transaction discounts or item discounts Fixed Price,CHAR,1,,O,Fixed price indicator for
 * the item irrespective of the qty 340b price,CHAR,18,2,O,The 340B Drug Pricing Program
 * is a federal program that requires drug manufacturers to sell outpatient drugs at
 * reduced prices to certain health care organizations. Price Unit,CHAR,5,,M,Indicates
 * Price per each qty/unit. Currency,CHAR,5,0,M,"The currency that applies to the document
 * (for example, to a sales order or an invoice)." Inventory type,CHAR,1,,M,"This
 * Indicates whether the material is stocked, True dropship or MPB Shared item" Minimum
 * Order Quantity,CHAR,17,,O,The minimum quantity a customer may order.
 *
 *
 * The class properties are generated like below
 *
 * @Meta(order=1, length = 10, name="Sold To Customer")
 * @Size(max = 35) private <Type> soldToCustomer; Type can be inferred by the Description
 * column,generate type as LocalDate where description of column infer it as Date the Meta
 * length value comes from Length column in csv, order is the line number of csv and name
 * is the Description column name of property comes from Description column Size max value
 * comes from Length column in csv if the column is String For Mandatory (M) Or Optional
 * (O) column if value is M add @NotBlank the column is String else @NotNull for other
 * data types For Number generate @Digits
 *
 *
 * Add @Column with name compatible with postgres DB column name Once The class is
 * generated, Generate a create Table sql command for the same compatible with postgres DB
 */
@Data
@ToString
@Entity
@Table(name = "catalog_price")
public class CatalogPrice {

	public static final DateTimeFormatter DATE_FORMAT_1 = DateTimeFormatter
		.ofPattern("[MM/dd/yyyy][MM/d/yyyy][M/dd/yyyy][M/d/yyyy]");

	@Id
	@GeneratedValue(generator = "catalog_price_id_seqkoi ")
	@SequenceGenerator(name = "catalog_price_id_seq", sequenceName = "catalog_price_id_seq", allocationSize = 1)
	private Long id;

	@Column(name = "tenant_id")
	public String tenantId;

	@Size(max = 10)
	@NotBlank
	@Column(name = "sold_to_customer")
	private String soldToCustomer;

	@Size(max = 35)
	@NotBlank
	@Column(name = "sold_to_customer_name")
	private String soldToCustomerName;

	@Size(max = 10)
	@NotBlank
	@Column(name = "ship_to_customer")
	private String shipToCustomer;

	@Size(max = 35)
	@NotBlank
	@Column(name = "ship_to_customer_name")
	private String shipToCustomerName;

	@Size(max = 10)
	@Column(name = "parent_customer")
	private String parentCustomer;

	@Size(max = 35)
	@Column(name = "parent_customer_name")
	private String parentCustomerName;

	@Size(max = 4)
	@NotBlank
	@Column(name = "sales_org")
	private String salesOrg;

	@Size(max = 2)
	@NotBlank
	@Column(name = "dist_channel")
	private String distChannel;

	@Size(max = 2)
	@NotBlank
	@Column(name = "division")
	private String division;

	@Size(max = 18)
	@NotBlank
	@Column(name = "sap_material")
	private String sapMaterial;

	@Size(max = 30)
	@Column(name = "variant")
	private String variant;

	@Size(max = 30)
	@Column(name = "variant_description")
	private String variantDescription;

	@NotBlank
	@Column(name = "material_identifier_for_cgat")
	private String materialIdentifierForCgat;

	@Size(max = 18)
	@NotBlank
	@Column(name = "ndc")
	private String ndc;

	@Size(max = 18)
	@Column(name = "udi")
	private String udi;

	@Size(max = 40)
	@NotBlank
	@Column(name = "material_description_in_language_en")
	private String materialDescriptionInLanguageEn;

	@Size(max = 3)
	@NotBlank
	@Column(name = "sell_unit")
	private String sellUnit;

	@Size(max = 10)
	@Column(name = "contract_no")
	private String contractNo;

	@Size(max = 4)
	@Column(name = "contract_type")
	private String contractType;

	@Size(max = 40)
	@Column(name = "contract_type_description")
	private String contractTypeDescription;

	@Column(name = "contract_valid_from")
	private LocalDate contractValidFrom;

	@Column(name = "contract_valid_to")
	private LocalDate contractValidTo;

	@Size(max = 10)
	@Column(name = "lead_number_mpb")
	private String leadNumberMpb;

	@Size(max = 10)
	@Column(name = "buying_group")
	private String buyingGroup;

	@Size(max = 40)
	@Column(name = "buying_group_description")
	private String buyingGroupDescription;

	@Size(max = 3)
	@Column(name = "customer_identifier")
	private String customerIdentifier;

	@NotNull
	@Column(name = "pricing_date")
	private LocalDate pricingDate;

	@Digits(integer = 16, fraction = 2)
	@NotNull
	@Column(name = "net_price")
	private BigDecimal netPrice;

	@Digits(integer = 16, fraction = 2)
	@Column(name = "regular_price")
	private BigDecimal regularPrice;

	@Digits(integer = 16, fraction = 2)
	@Column(name = "contract_price")
	private BigDecimal contractPrice;

	@Digits(integer = 16, fraction = 2)
	@NotNull
	@Column(name = "wac_price")
	private BigDecimal wacPrice;

	@Digits(integer = 16, fraction = 2)
	@Column(name = "discounts")
	private BigDecimal discounts;

	@Size(max = 1)
	@Column(name = "fixed_price")
	private String fixedPrice;

	@Digits(integer = 16, fraction = 2)
	@Column(name = "price_340b")
	private BigDecimal price340b;

	@Size(max = 5)
	@NotBlank
	@Column(name = "price_unit")
	private String priceUnit;

	@Size(max = 5)
	@NotBlank
	@Column(name = "currency")
	private String currency;

	@Size(max = 1)
	@NotBlank
	@Column(name = "inventory_type")
	private String inventoryType;

	@Size(max = 17)
	@Column(name = "minimum_order_quantity")
	private String minimumOrderQuantity;

}