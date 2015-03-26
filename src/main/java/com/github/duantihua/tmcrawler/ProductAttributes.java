package com.github.duantihua.tmcrawler;

public class ProductAttributes {

	public static final ProductAttribute ImageUrl = new ProductAttribute("imageUrl","缩略图","image");
	public static final ProductAttribute Title = new ProductAttribute("title","商品名称");
	public static final ProductAttribute Code = new ProductAttribute("code","商品货号");
	public static final ProductAttribute Price = new ProductAttribute("price","天猫价","double");
	public static final ProductAttribute DefaultPrice = new ProductAttribute("defaultPrice","最低价","double");
	public static final ProductAttribute Color = new ProductAttribute("color","颜色分类");
	public static final ProductAttribute MonthSaleCnt = new ProductAttribute("monthSaleCnt","月销量","long");
	public static final ProductAttribute Size = new ProductAttribute("size","尺码");
	public static final ProductAttribute StoreCnt = new ProductAttribute("storeCnt","库存","long");
	public static final ProductAttribute Href = new ProductAttribute("href","链接","url");

}

