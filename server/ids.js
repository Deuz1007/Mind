import { randomBytes } from 'crypto';

const alphabet = 'useandom-26T198340PX75pxJACKVERYMINDBUSHWOLF_GQZbfghjklqvwyzric';

const generate = () => [...randomBytes(16)].map((b) => alphabet[b & 63]).join('');

export default generate;