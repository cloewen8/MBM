package ca.cjloewen.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;

/**
 * A recipe that preserves damageable tools when crafting.
 * Instead causing the tool to take damage.
 */
public class ToolRecipe extends ShapelessRecipe {
	public ToolRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn,
			NonNullList<Ingredient> recipeItemsIn) {
		super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
	}
	
	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack item = inv.getStackInSlot(i);
			// Preserve tools.
			if (item.isDamageable()) {
				PlayerEntity player = ForgeHooks.getCraftingPlayer();
				// Damage the item.
				if (!item.attemptDamageItem(1, player.getRNG(),
						player instanceof ServerPlayerEntity ? (ServerPlayerEntity) player : null)) {
					nonnulllist.set(i, item.copy());
				}
			} else if (item.hasContainerItem()) {
				nonnulllist.set(i, item.getContainerItem());
			}
		}

		return nonnulllist;
	}
	
	/**
	 * Identical to the ShapelessRecipe serializer.
	 */
	public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ToolRecipe> {
	      public static final String NAME = "crafting_tool";
	      
	      public ToolRecipe read(ResourceLocation recipeId, JsonObject json) {
	    	  System.out.println(json);
	         String s = JSONUtils.getString(json, "group", "");
	         NonNullList<Ingredient> nonnulllist = readIngredients(JSONUtils.getJsonArray(json, "ingredients"));
	         if (nonnulllist.isEmpty()) {
	            throw new JsonParseException("No ingredients for shapeless recipe");
	         } else if (nonnulllist.size() > ShapedRecipe.MAX_WIDTH * ShapedRecipe.MAX_HEIGHT) {
	            throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + (ShapedRecipe.MAX_WIDTH * ShapedRecipe.MAX_HEIGHT));
	         } else {
	            ItemStack itemstack = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
	            return new ToolRecipe(recipeId, s, itemstack, nonnulllist);
	         }
	      }

	      private static NonNullList<Ingredient> readIngredients(JsonArray p_199568_0_) {
	         NonNullList<Ingredient> nonnulllist = NonNullList.create();

	         for(int i = 0; i < p_199568_0_.size(); ++i) {
	            Ingredient ingredient = Ingredient.deserialize(p_199568_0_.get(i));
	            if (!ingredient.hasNoMatchingItems()) {
	               nonnulllist.add(ingredient);
	            }
	         }

	         return nonnulllist;
	      }

	      public ToolRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
	         String s = buffer.readString(32767);
	         int i = buffer.readVarInt();
	         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

	         for(int j = 0; j < nonnulllist.size(); ++j) {
	            nonnulllist.set(j, Ingredient.read(buffer));
	         }

	         ItemStack itemstack = buffer.readItemStack();
	         return new ToolRecipe(recipeId, s, itemstack, nonnulllist);
	      }

	      public void write(PacketBuffer buffer, ToolRecipe recipe) {
	         buffer.writeString(recipe.group);
	         buffer.writeVarInt(recipe.recipeItems.size());

	         for(Ingredient ingredient : recipe.recipeItems) {
	            ingredient.write(buffer);
	         }

	         buffer.writeItemStack(recipe.recipeOutput);
	      }
	   }
}
