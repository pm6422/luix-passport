import { useState, useRef } from "react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { toBase64 } from "@/lib/file-utils"
import { IconPencil, IconCamera } from "@tabler/icons-react"

interface Props {
  defaultValue?: string
  onValueChange?: (value?: File) => void
  avatarClassName?: string
  className?: string
}

export function AvatarUploader({
  defaultValue,
  onValueChange,
  avatarClassName,
  className
}: Props) {
  const [imageSrc, setImageSrc] = useState<string | undefined>(defaultValue)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const handleChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const file = e.target.files[0]
      const base64 = await toBase64(file) as string
      // set image base64 data
      setImageSrc(base64)
      onValueChange?.(file)
    }
  }

  return (
    <div className={`relative ${className ?? "size-40"}`}>
      <Avatar className={`w-full h-full ${avatarClassName}`}>
        <AvatarImage src={imageSrc} className="object-cover" />
        <AvatarFallback className="bg-secondary">
          <IconCamera className="size-16" />
        </AvatarFallback>
      </Avatar>

      <Button
        type="button"
        variant="ghost"
        size="icon"
        className="absolute bottom-1 end-0 rounded-full p-1 border border-input bg-secondary/80 hover:bg-secondary"
        onClick={() => fileInputRef.current?.click()}
      >
        <IconPencil className="size-5 text-secondary-foreground" />
      </Button>

      <Input
        ref={fileInputRef}
        type="file"
        className="hidden"
        onChange={handleChange}
        accept="image/png,image/jpeg,image/jpg,image/gif"
      />
    </div>
  )
}
