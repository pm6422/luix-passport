import { Badge } from "@/components/ui/badge";
import { IconCircleCheck, IconCircleX } from "@tabler/icons-react";
import { cn } from '@/lib/utils.ts'

interface StatusBadgeProps {
  value?: boolean | null;
  className?: string;
}

export function StatusBadge({ value, className }: StatusBadgeProps) {
  return (
    <Badge
      variant="outline"
      className={cn(
        "flex gap-1 px-1.5 text-muted-foreground [&_svg]:size-3 capitalize",
        className
      )}
    >
      {value ? (
        <IconCircleCheck className="text-green-500 dark:text-green-400" />
      ) : (
        <IconCircleX className="text-red-500 dark:text-red-400" />
      )}
      {String(value)}
    </Badge>
  );
}