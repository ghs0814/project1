import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import './App.css';

const API_URL = 'http://localhost:8080/api/products';

type Product = {
    productId: number;
    productName: string;
    price: number;
    quantity: number;
};

type ProductRowProps = {
    product: Product;
    onEdit: (product: Product) => void;
    onDelete: (productId: number) => void;
    disabled: boolean;
    deleting: boolean;
};

function ProductRow({ /// 자식 컴포넌트
    product,
    onEdit,
    onDelete,
    disabled,
    deleting
}: ProductRowProps) {

    return (
        <tr>
            <td>{product.productId}</td>
            <td>{product.productName}</td>
            <td>{product.price.toLocaleString()}원</td>
            <td>{product.quantity}</td>
            <td>
                <button
                    type="button"
                    onClick={() => onEdit(product)}
                    disabled={disabled}
                >
                    수정
                </button>

                <button
                    type="button"
                    onClick={() => onDelete(product.productId)}
                    disabled={disabled}
                >
                    {deleting ? '삭제 중...' : '삭제'}
                </button>
            </td>
        </tr>
    );
}

function App() {

    // 상품 목록
    const [products, setProducts] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [showProducts, setShowProducts] = useState(true);

    // 입력 폼
    const [productName, setProductName] = useState('');
    const [price, setPrice] = useState('');
    const [quantity, setQuantity] = useState('');

    // 등록 및 수정
    const [editingId, setEditingId] = useState<number | null>(null);
    const [saving, setSaving] = useState(false);
    const [formError, setFormError] = useState<string | null>(null);

    // 삭제
    const [deletingId, setDeletingId] = useState<number | null>(null);
    const [deleteError, setDeleteError] = useState<string | null>(null);

    const busy = saving || deletingId !== null;

    // 1. 상품 목록 조회
    useEffect(() => {

        const controller = new AbortController();

        async function loadProducts() {

            try {
                setLoading(true);
                setError(null);

                const response = await fetch(API_URL, {
                    signal: controller.signal
                });

                if (!response.ok) {
                    throw new Error('상품 조회에 실패했습니다.');
                }

                const data: Product[] = await response.json();

                if (!controller.signal.aborted) {
                    setProducts(data);
                }

            } catch (err) {

                if (controller.signal.aborted) {
                    return;
                }

                setError(
                    err instanceof Error
                        ? err.message
                        : '상품 조회 중 오류가 발생했습니다.'
                );

            } finally {

                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            }
        }

        void loadProducts();

        return () => {
            controller.abort();
        };

    }, []);

    // 2. 입력 폼 초기화
    function resetForm() {
        setProductName('');
        setPrice('');
        setQuantity('');
    }

    // 3. 수정 모드 시작
    function handleEdit(product: Product) {

        if (busy) {
            return;
        }

        setEditingId(product.productId);
        setProductName(product.productName);
        setPrice(String(product.price));
        setQuantity(String(product.quantity));
        setFormError(null);
    }

    // 4. 수정 취소
    function handleCancelEdit() {

        setEditingId(null);
        resetForm();
        setFormError(null);
    }

    // 5. 상품 등록 또는 수정
    async function handleSubmit(event: FormEvent<HTMLFormElement>) {

        event.preventDefault();

        if (busy) {
            return;
        }

        setFormError(null);

        const name = productName.trim();
        const priceNumber = Number(price);
        const quantityNumber = Number(quantity);

        if (!name || price === '' || quantity === '') {
            setFormError('모든 항목을 입력해주세요.');
            return;
        }

        if (name.length > 100) {
            setFormError('상품명은 100자 이하여야 합니다.');
            return;
        }

        if (
            !Number.isInteger(priceNumber) ||
            !Number.isInteger(quantityNumber) ||
            priceNumber < 0 ||
            quantityNumber < 0 ||
            priceNumber > 2147483647 ||
            quantityNumber > 2147483647
        ) {
            setFormError('가격과 수량은 0 이상의 정수여야 합니다.');
            return;
        }

        const requestData = {
            productName: name,
            price: priceNumber,
            quantity: quantityNumber
        };

        const isEditing = editingId !== null;

        try {
            setSaving(true);

            const url = isEditing
                ? `${API_URL}/${editingId}`
                : API_URL;

            const response = await fetch(url, {
                method: isEditing ? 'PUT' : 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            if (!response.ok) {

                const errorBody: { message?: string } =
                    await response.json().catch(() => ({}));

                throw new Error(
                    errorBody.message ?? '상품 저장에 실패했습니다.'
                );
            }

            const savedProduct: Product = await response.json();

            if (isEditing) {

                // 기존 상품 교체
                setProducts(prev =>
                    prev.map(product =>
                        product.productId === savedProduct.productId
                            ? savedProduct
                            : product
                    )
                );

            } else {

                // 새로운 상품 추가
                setProducts(prev => [...prev, savedProduct]);
            }

            setEditingId(null);
            resetForm();
            setShowProducts(true);

        } catch (err) {

            setFormError(
                err instanceof Error
                    ? err.message
                    : '상품 저장 중 오류가 발생했습니다.'
            );

        } finally {

            setSaving(false);
        }
    }

    // 6. 상품 삭제
    async function handleDelete(productId: number) {

        if (busy) {
            return;
        }

        const confirmed = window.confirm(
            `상품 ID ${productId}를 삭제하시겠습니까?`
        );

        if (!confirmed) {
            return;
        }

        try {
            setDeletingId(productId);
            setDeleteError(null);

            const response = await fetch(
                `${API_URL}/${productId}`,
                {
                    method: 'DELETE'
                }
            );

            if (!response.ok) {

                const errorBody: { message?: string } =
                    await response.json().catch(() => ({}));

                throw new Error(
                    errorBody.message ?? '상품 삭제에 실패했습니다.'
                );
            }

            // 삭제된 상품을 목록에서 제거
            setProducts(prev =>
                prev.filter(product =>
                    product.productId !== productId
                )
            );

            // 수정 중인 상품을 삭제했다면 폼 초기화
            if (editingId === productId) {
                setEditingId(null);
                resetForm();
                setFormError(null);
            }

        } catch (err) {

            setDeleteError(
                err instanceof Error
                    ? err.message
                    : '상품 삭제 중 오류가 발생했습니다.'
            );

        } finally {

            setDeletingId(null);
        }
    }

    return (
        <main className="container">

            <h1>Project1 재고관리 시스템</h1>
            <p>React + Spring Boot + PostgreSQL</p>

            <section className="form-section">

                <h2>
                    {editingId === null ? '상품 등록' : '상품 수정'}
                </h2>

                <form onSubmit={handleSubmit}>

                    <label htmlFor="productName">상품명</label>
                    <input
                        id="productName"
                        type="text"
                        value={productName}
                        onChange={(e) => setProductName(e.target.value)}
                        maxLength={100}
                        disabled={busy}
                        required
                    />

                    <label htmlFor="price">가격</label>
                    <input
                        id="price"
                        type="number"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        min="0"
                        max="2147483647"
                        step="1"
                        disabled={busy}
                        required
                    />

                    <label htmlFor="quantity">수량</label>
                    <input
                        id="quantity"
                        type="number"
                        value={quantity}
                        onChange={(e) => setQuantity(e.target.value)}
                        min="0"
                        max="2147483647"
                        step="1"
                        disabled={busy}
                        required
                    />

                    <div className="form-buttons">

                        <button
                            type="submit"
                            disabled={busy || loading}
                        >
                            {saving
                                ? '저장 중...'
                                : editingId === null
                                    ? '상품 등록'
                                    : '수정 완료'}
                        </button>

                        {editingId !== null && (
                            <button
                                type="button"
                                onClick={handleCancelEdit}
                                disabled={busy}
                            >
                                수정 취소
                            </button>
                        )}

                    </div>

                </form>

                {formError && (
                    <p className="error">{formError}</p>
                )}

            </section>

            <section>

                <h2>상품 목록</h2>

                <button
                    type="button"
                    onClick={() => setShowProducts(prev => !prev)}
                >
                    {showProducts ? '목록 숨기기' : '목록 보기'}
                </button>

                {loading && (
                    <p>상품 목록을 불러오는 중...</p>
                )}

                {error && (
                    <p className="error">{error}</p>
                )}

                {deleteError && (
                    <p className="error">{deleteError}</p>
                )}

                {showProducts && !loading && !error && (

                    products.length === 0
                        ? <p>등록된 상품이 없습니다.</p>
                        : (
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>상품명</th>
                                        <th>가격</th>
                                        <th>수량</th>
                                        <th>관리</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {products.map(product => (
                                        <ProductRow
                                            key={product.productId}
                                            product={product}
                                            onEdit={handleEdit}
                                            onDelete={handleDelete}
                                            disabled={busy}
                                            deleting={
                                                deletingId === product.productId
                                            }
                                        />
                                    ))}
                                </tbody>
                            </table>
                        )
                )}

            </section>

        </main>
    );
}

export default App;