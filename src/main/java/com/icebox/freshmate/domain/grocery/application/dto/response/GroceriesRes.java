package com.icebox.freshmate.domain.grocery.application.dto.response;

import java.util.List;

import org.springframework.data.domain.Slice;

import com.icebox.freshmate.domain.grocery.domain.Grocery;
import com.icebox.freshmate.domain.grocery.domain.GroceryImage;
import com.icebox.freshmate.domain.image.application.dto.response.ImageRes;

public record GroceriesRes(
	List<GroceryRes> groceries,
	boolean hasNext
) {

	public static GroceriesRes from(Slice<Grocery> groceries) {
		List<GroceryRes> groceriesRes = groceries.stream()
			.map(grocery ->
			{
				List<ImageRes> groceryImagesRes = getGroceryImagesRes(grocery);

				return GroceryRes.of(grocery, groceryImagesRes);
			})
			.toList();

		return new GroceriesRes(groceriesRes, groceries.hasNext());
	}

	private static List<ImageRes> getGroceryImagesRes(Grocery grocery) {
		List<GroceryImage> groceryImages = grocery.getGroceryImages();

		return groceryImages.stream()
			.map(groceryImage -> ImageRes.of(groceryImage.getFileName(), groceryImage.getPath()))
			.toList();
	}
}
