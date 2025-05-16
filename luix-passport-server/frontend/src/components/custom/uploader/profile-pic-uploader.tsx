import { useState, useRef, forwardRef } from "react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/custom/button";
import { Input } from "@/components/ui/input";
import { toBase64 } from "@/lib/utils";
import { IconPencil, IconUserScan } from "@tabler/icons-react";

type Props = {
  defaultValue?: string;
  onValueChange?: (value?: File) => void;
};

export const ProfilePicUploader = forwardRef<HTMLDivElement, Props>(
  ({ defaultValue, onValueChange }, ref) => {
    const [imageSrc, setImageSrc] = useState<string | undefined>(defaultValue);
    const fileInputRef = useRef<HTMLInputElement>(null);

    const handleChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
      if (e.target.files && e.target.files.length > 0) {
        const file = e.target.files[0];
        const base64 = (await toBase64(file)) as string;
        setImageSrc(base64);
        onValueChange?.(file);
      }
    };

    return (
      <div className="relative size-40" ref={ref}>
        <Avatar className={`w-full h-full`}>
          <AvatarImage src={imageSrc} className="object-cover" />
          <AvatarFallback className="bg-secondary">
            <IconUserScan className="size-16" />
          </AvatarFallback>
        </Avatar>
        <Button
          type="button"
          variant="ghost"
          size="icon"
          className="absolute bottom-1 right-0 rounded-full p-1 bg-secondary-foreground/90 hover:bg-secondary-foreground"
          onClick={() => fileInputRef.current?.click()}
        >
          <IconPencil className="size-5 text-gray-400" />
        </Button>
        <Input
          ref={fileInputRef}
          type="file"
          className="hidden"
          onChange={handleChange}
          accept="image/png,image/jpeg,image/jpg,image/gif"
        />
      </div>
    );
  }
);

ProfilePicUploader.displayName = "ProfilePicUploader";