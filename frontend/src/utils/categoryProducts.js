import { categoryAPI, productAPI } from "../api/index.js";

export async function loadProductsForCategories(parentCategoryIds) {
  const parentIds = Array.isArray(parentCategoryIds) ? parentCategoryIds : [parentCategoryIds];
  const childResponses = await Promise.all(parentIds.map((parentId) => categoryAPI.getChildren(parentId)));
  const categoryIds = childResponses
    .flatMap((response, index) =>
      response.data.length > 0 ? response.data.map((category) => category.id) : [parentIds[index]]
    );

  const responses = await Promise.all(
    categoryIds.map((categoryId) => productAPI.getByCategory(categoryId))
  );

  const productsById = new Map();
  responses
    .flatMap((response) => response.data)
    .forEach((product) => productsById.set(product.id, product));

  return Array.from(productsById.values()).sort((a, b) => a.id - b.id);
}

export const loadProductsForCategory = loadProductsForCategories;
