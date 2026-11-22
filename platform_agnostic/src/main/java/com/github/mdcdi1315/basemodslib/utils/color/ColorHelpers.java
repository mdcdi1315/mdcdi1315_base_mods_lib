package com.github.mdcdi1315.basemodslib.utils.color;

import com.github.mdcdi1315.basemodslib.utils.Extensions;
import com.github.mdcdi1315.basemodslib.codecs.CodecUtils;
import com.github.mdcdi1315.basemodslib.codecs.IntRangeCodec;
import com.github.mdcdi1315.basemodslib.utils.annotations.Pure;
import com.github.mdcdi1315.basemodslib.codecs.AlternativeCodec;

import com.mojang.serialization.Codec;

/**
 * Defines utilities around colors.
 */
public final class ColorHelpers
{
    // Do not let anyone be able to instantiate this class.
    private ColorHelpers() {}

    private static final int ARGB_RED_SHIFT = 16;
    private static final int ARGB_GREEN_SHIFT = 8;
    private static final int ARGB_ALPHA_SHIFT = 24;
    private static final int RGB_MASK_ARGB = 0x00FFFFFF;
    private static final int COLOR_CHANNEL_MASK = 0x000000FF;

    private static Codec<Integer> GetRecordCodec()
    {
        IntRangeCodec field_codec = new IntRangeCodec(0, COLOR_CHANNEL_MASK);
        return CodecUtils.CreateCodecDirect(
                field_codec.optionalFieldOf("alpha", COLOR_CHANNEL_MASK).forGetter(ColorHelpers::GetAlpha),
                field_codec.fieldOf("red").forGetter(ColorHelpers::GetRed),
                field_codec.fieldOf("green").forGetter(ColorHelpers::GetGreen),
                field_codec.fieldOf("blue").forGetter(ColorHelpers::GetBlue),
                ColorHelpers::CreateARGB
        );
    }

    /**
     * Provides an ARGB color codec, whose color channels are defined as fields.
     */
    public static final Codec<Integer> ARGB_RECORD_CODEC = GetRecordCodec();

    /**
     * Provides an ARGB color codec that normally tries to decode directly an integer as a ARGB color, otherwise tries to decode it as a {@link #ARGB_RECORD_CODEC}.
     */
    public static final Codec<Integer> ARGB_CODEC = new AlternativeCodec<>(Codec.INT, ARGB_RECORD_CODEC);

    /**
     * From a ARGB color, returns the RGB part only.
     * @param argb The ARGB color.
     * @return The RGB value of the color.
     */
    @Pure
    public static int GetRGB(int argb) { return argb & RGB_MASK_ARGB; }

    /**
     * From a RGB color, constructs a ARGB color suitable to be used where a ARGB color is required as input.
     * @param rgb The RGB color to convert.
     * @return The converted color whose RGB color channels are took from the {@code rgb} parameter.
     */
    @Pure
    public static int RGBToARGB(int rgb) { return CreateARGB(255, rgb); }

    /**
     * From an alpha color channel, and a RGB color, it creates a ARGB value.
     * @param alpha The alpha channel.
     * @param rgb The packed RGB color.
     * @return The ARGB color.
     */
    @Pure
    public static int CreateARGB(int alpha, int rgb) { return alpha << ARGB_ALPHA_SHIFT | (rgb & RGB_MASK_ARGB); }

    /**
     * From four distinct color channels, constructs a ARGB value.
     * @param alpha The alpha channel.
     * @param red The red channel.
     * @param green The green channel.
     * @param blue The blue channel.
     * @return The ARGB color.
     */
    @Pure
    public static int CreateARGB(int alpha, int red, int green, int blue) { return alpha << ARGB_ALPHA_SHIFT | red << ARGB_RED_SHIFT | green << ARGB_GREEN_SHIFT | blue; }

    /**
     * From four distinct color channels, constructs a ARGB value.
     * @param alpha The alpha channel.
     * @param red The red channel.
     * @param green The green channel.
     * @param blue The blue channel.
     * @return The ARGB color.
     */
    @Pure
    public static int CreateARGB(byte alpha, byte red, byte green, byte blue)
    {
        return CreateARGB(
                alpha & COLOR_CHANNEL_MASK,
                red & COLOR_CHANNEL_MASK,
                green & COLOR_CHANNEL_MASK,
                blue & COLOR_CHANNEL_MASK
        );
    }

    /**
     * From four distinct color channels, constructs a ARGB value.
     * @param alpha The alpha channel.
     * @param red The red channel.
     * @param green The green channel.
     * @param blue The blue channel.
     * @return The ARGB color.
     * @apiNote This method variant accepts floating-point values instead,
     * so the acceptable range of values for all of these is the range [0..1].
     * @see #FloatColorChannelTo8BitColor(float) 
     */
    @Pure
    public static int CreateARGB(float alpha, float red, float green, float blue) 
    {
        return CreateARGB(
                FloatColorChannelTo8BitColor(alpha),
                FloatColorChannelTo8BitColor(red),
                FloatColorChannelTo8BitColor(green),
                FloatColorChannelTo8BitColor(blue)
        );
    }
    
    /**
     * From three distinct color channels, constructs a RGB value.
     * @param red The red channel.
     * @param green The green channel.
     * @param blue The blue channel.
     * @return The RGB color.
     */
    @Pure
    public static int CreateRGB(int red, int green, int blue) { return CreateARGB(0, red, green, blue); }

    /**
     * From three distinct color channels, constructs a RGB value.
     * @param red The red channel.
     * @param green The green channel.
     * @param blue The blue channel.
     * @return The RGB color.
     */
    @Pure
    public static int CreateRGB(byte red, byte green, byte blue) { return CreateARGB(0, red, green, blue); }

    /**
     * From three distinct color channels, constructs a RGB value.
     * @param red The red channel.
     * @param green The green channel.
     * @param blue The blue channel.
     * @return The ARGB color.
     * @apiNote This method variant accepts floating-point values instead,
     * so the acceptable range of values for all of these is the range [0..1].
     * @see #FloatColorChannelTo8BitColor(float)
     */
    @Pure
    public static int CreateRGB(float red, float green, float blue) { return CreateARGB(0, red, green, blue); }

    /**
     * From a ARGB color, it creates a BGRA color.
     * @param argb The ARGB color to reinterpret.
     * @return The equivalent BGRA color created from the provided ARGB color.
     */
    @Pure
    public static int CreateBGRA(int argb) { return GetBlue(argb) << 24 | GetGreen(argb) << 16 | GetRed(argb) << 8 | GetAlpha(argb); }

    /**
     * From a BGRA color, it creates a ARGB color.
     * @param bgra The BGRA color to reinterpret.
     * @return The equivalent ARGB color created from the provided BGRA color.
     */
    @Pure
    public static int BGRAToARGB(int bgra)
    {
        return (bgra & COLOR_CHANNEL_MASK) << ARGB_ALPHA_SHIFT |
                ((bgra >> 8) & COLOR_CHANNEL_MASK) << ARGB_RED_SHIFT |
                ((bgra >> 16) & COLOR_CHANNEL_MASK) << ARGB_GREEN_SHIFT |
                ((bgra >> 24) & COLOR_CHANNEL_MASK);
    }

    /**
     * From a ARGB color, it creates a ABGR color.
     * @param argb The ARGB color to reinterpret.
     * @return The equivalent ABGR color created from the provided ARGB color.
     */
    @Pure
    public static int CreateABGR(int argb) { return GetAlpha(argb) << 24 | GetBlue(argb) << 16 | GetGreen(argb) << 8 | GetRed(argb); }

    /**
     * From a ABGR color, it creates a ARGB color.
     * @param abgr The ABGR color to reinterpret.
     * @return The equivalent ARGB color created from the provided ABGR color.
     */
    @Pure
    public static int ABGRToARGB(int abgr)
    {
        return (abgr & COLOR_CHANNEL_MASK) << ARGB_RED_SHIFT |
                ((abgr >> 8) & COLOR_CHANNEL_MASK) << ARGB_GREEN_SHIFT |
                ((abgr >> 24) & COLOR_CHANNEL_MASK) << ARGB_ALPHA_SHIFT |
                ((abgr >> 16) & COLOR_CHANNEL_MASK);
    }

    /**
     * From a ARGB color, gets the alpha color channel.
     * @param argb The ARGB color to get its alpha color channel.
     * @return The alpha color channel value.
     */
    @Pure
    public static int GetAlpha(int argb) { return (argb >> ARGB_ALPHA_SHIFT) & COLOR_CHANNEL_MASK; }

    /**
     * From a ARGB/RGB color, gets the red color channel.
     * @param argb The ARGB/RGB color to get its red color channel.
     * @return The red color channel value.
     */
    @Pure
    public static int GetRed(int argb) { return (argb >> ARGB_RED_SHIFT) & COLOR_CHANNEL_MASK; }

    /**
     * From a ARGB/RGB color, gets the green color channel.
     * @param argb The ARGB/RGB color to get its green color channel.
     * @return The green color channel value.
     */
    @Pure
    public static int GetGreen(int argb) { return (argb >> ARGB_GREEN_SHIFT) & COLOR_CHANNEL_MASK; }

    /**
     * From a ARGB/RGB color, gets the blue color channel.
     * @param argb The ARGB/RGB color to get its blue color channel.
     * @return The blue color channel value.
     */
    @Pure
    public static int GetBlue(int argb) { return argb & COLOR_CHANNEL_MASK; }

    /**
     * Inverts the specified ARGB color.
     * @param argb The ARGB color to be inverted.
     * @return The inverted ARGB color.
     */
    @Pure
    public static int Invert(int argb)
    {
        return CreateARGB(
                COLOR_CHANNEL_MASK - GetAlpha(argb),
                COLOR_CHANNEL_MASK - GetRed(argb),
                COLOR_CHANNEL_MASK - GetGreen(argb),
                COLOR_CHANNEL_MASK - GetBlue(argb)
        );
    }

    /**
     * Creates a gray color from the specified brightness value.
     * @param brightness The brightness of the gray color.
     * @return A ARGB color that is a gray-scaled color.
     */
    @Pure
    public static int CreateGrayColor(int brightness)
    {
        int color = brightness & COLOR_CHANNEL_MASK;
        return CreateARGB(COLOR_CHANNEL_MASK, color, color, color);
    }

    /**
     * Creates a gray color from the specified brightness value.
     * @param brightness The brightness of the gray color.
     * @return A ARGB color that is a gray-scaled color.
     */
    @Pure
    public static int CreateGrayColor(float brightness)
    {
        int color = FloatColorChannelTo8BitColor(brightness);
        return CreateARGB(COLOR_CHANNEL_MASK, color, color, color);
    }

    /**
     * Maps the specified color to the gray scale.
     * @param argb The ARGB color to be mapped to the gray scale.
     * @return A ARGB color that is the mapped result of {@code argb}.
     */
    @Pure
    public static int Grayscale(int argb)
    {
        int c = (GetRed(argb) + GetGreen(argb) + GetBlue(argb)) / (COLOR_CHANNEL_MASK * 3);
        return CreateARGB(GetAlpha(argb), c, c, c);
    }

    /**
     * Maps the specified color to the gray scale.
     * @param argb The ARGB color to be mapped to the gray scale.
     * @return A ARGB color that is the mapped result of {@code argb}.
     * @apiNote The difference between this and {@link #Grayscale(int)} is that
     * this one makes the grayscale on weighted values, so it better approaches
     * the eye's perception.
     */
    @Pure
    public static int GrayscaleWeighted(int argb)
    {
        int c = (int)(GetRed(argb) * 0.3f + GetGreen(argb) * 0.59f + GetBlue(argb) * 0.11f);
        return CreateARGB(GetAlpha(argb), c, c, c);
    }

    /**
     * Treats the alpha channel of the ARGB color as the brightness of each other color channel, and returns an ARGB color representing the new color.
     * @param argb The ARGB color to convert.
     * @return The converted ARGB color.
     */
    @Pure
    public static int TreatAlphaAsBrightness(int argb)
    {
        int alpha = GetAlpha(argb);
        return CreateARGB(
                COLOR_CHANNEL_MASK,
                (alpha * GetRed(argb)) / COLOR_CHANNEL_MASK,
                (alpha * GetGreen(argb)) / COLOR_CHANNEL_MASK,
                (alpha * GetBlue(argb)) / COLOR_CHANNEL_MASK
        );
    }

    /**
     * Multiplies two ARGB colors together.
     * @param color_a The first color.
     * @param color_b The second color.
     * @return The multiplied result.
     */
    @Pure
    public static int Multiply(int color_a, int color_b)
    {
        return CreateARGB(
                (GetAlpha(color_a) * GetAlpha(color_b)) / COLOR_CHANNEL_MASK,
                (GetRed(color_a) * GetRed(color_b)) / COLOR_CHANNEL_MASK,
                (GetGreen(color_a) * GetGreen(color_b)) / COLOR_CHANNEL_MASK,
                (GetBlue(color_a) * GetBlue(color_b)) / COLOR_CHANNEL_MASK
        );
    }

    /**
     * Adds two colors together safely.
     * @param color_a The first color.
     * @param color_b The second color.
     * @return A color whose channels were the addition of each channel of the provided colors.
     * @apiNote You can add two ARGB colors together by using the built-in + operator of the Java integer.
     * However, you may want the channels to not clip, so this does ensure that the addition does not
     * make the color to be distorted.
     */
    @Pure
    public static int AddSafe(int color_a, int color_b)
    {
        return CreateARGB(
                Extensions.Min(GetAlpha(color_a) + GetAlpha(color_b), COLOR_CHANNEL_MASK),
                Extensions.Min(GetRed(color_a) + GetRed(color_b), COLOR_CHANNEL_MASK),
                Extensions.Min(GetGreen(color_a) + GetGreen(color_b), COLOR_CHANNEL_MASK),
                Extensions.Min(GetBlue(color_a) + GetBlue(color_b), COLOR_CHANNEL_MASK)
        );
    }

    /**
     * Subtracts two colors together safely.
     * @param color_a The first color.
     * @param color_b The second color.
     * @return A color whose channels were the subtraction of each channel of the provided colors.
     * @apiNote You can subtract two ARGB colors together by using the built-in - operator of the Java integer.
     * However, you may want the channels to not clip, so this does ensure that the subtraction does not
     * make the color to be distorted.
     */
    @Pure
    public static int SubtractSafe(int color_a, int color_b)
    {
        return CreateARGB(
                Extensions.Max(GetAlpha(color_a) - GetAlpha(color_b), 0),
                Extensions.Max(GetRed(color_a) - GetRed(color_b), 0),
                Extensions.Max(GetGreen(color_a) - GetGreen(color_b), 0),
                Extensions.Max(GetBlue(color_a) - GetBlue(color_b), 0)
        );
    }

    /**
     * Scales the specified color channel by the specified value.
     * @param channel The color channel value to scale.
     * @param scale_by The value, in range [0..1], to scale the specified color channel by.
     * @return The scaled color channel.
     * @apiNote If the scaling yields values more than 255, 255 is returned; likewise, for
     * values less than 0, 0 is returned.
     */
    @Pure
    public static int ScaleColorChannel(int channel, float scale_by) { return Extensions.Clamp((int)(channel * scale_by), 0, COLOR_CHANNEL_MASK); }

    /**
     * Scales the specified ARGB color by the specified value.
     * @param argb The ARGB color to scale.
     * @param scale_by The value, in range [0..1], to scale the specified color channel by.
     * @return The scaled color.
     * @apiNote If the scaling on a channel yields values more than 255,
     * 255 is returned for that channel; likewise, for values less than 0, 0 is returned.
     */
    @Pure
    public static int ScaleARGB(int argb, float scale_by)
    {
        return CreateARGB(
                ScaleColorChannel(GetAlpha(argb), scale_by),
                ScaleColorChannel(GetRed(argb), scale_by),
                ScaleColorChannel(GetGreen(argb), scale_by),
                ScaleColorChannel(GetBlue(argb), scale_by)
        );
    }

    /**
     * Performs a linear mapping from the ARGB color {@code start} to the ARGB color {@code end}.
     * @param delta The value to linearly interpolate.
     * @param start The starting color.
     * @param end The ending color.
     * @return The linear mapping of {@code start} and {@code end} at {@code delta}, as a ARGB color.
     */
    @Pure
    @SuppressWarnings("SpellCheckingInspection")
    public static int Lerp(float delta, int start, int end)
    {
        return CreateARGB(
                Extensions.LerpInt(delta, GetAlpha(start), GetAlpha(end)),
                Extensions.LerpInt(delta, GetRed(start), GetRed(end)),
                Extensions.LerpInt(delta, GetGreen(start), GetGreen(end)),
                Extensions.LerpInt(delta, GetBlue(start), GetBlue(end))
        );
    }

    /**
     * Converts the specified floating-point color channel value to an 8-bit color channel value.
     * @param value The value to convert. Must be a value in the range [0..1].
     * @return The converted 8-bit color.
     * @apiNote Any values less than 0 are treated as 0, and any values greater than 1 are treated as 1.
     */
    @Pure
    public static int FloatColorChannelTo8BitColor(float value) { return Extensions.Clamp((int)(value * COLOR_CHANNEL_MASK), 0, COLOR_CHANNEL_MASK); }

    /**
     * Converts the specified 8-bit color channel value to a floating-point color channel value.
     * @param value The value to convert. Must be a value in the range [0..255].
     * @return The converted floating-point color.
     * @apiNote Any values less than 0 are treated as 0, and any values greater than 255 are treated as 255.
     */
    @Pure
    public static float Color8BitToFloatColor(int value) { return Extensions.Clamp(value, 0, COLOR_CHANNEL_MASK) / 255f; }
}
